/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.25   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.srcontroller;

import java.io.IOException;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;


final class CleanUpSR extends Thread {
    private final long id;

    CleanUpSR(long id) {
        this.id = id;
    }

    public void run() {
        CallableStatement stmt = null;
        try {
            SRController.logger.log(SRController.LOG_LEVEL_9, "SR cleanup stage 1.");
        } catch (LoggerException e) {
            Logger.logError("SR cleanup log error:", e);
        }
        synchronized (SRController.connection) {
            try {
                SRController.logger.log(SRController.LOG_LEVEL_9, "SR cleanup stage 2.");
                stmt = SRController.connection.prepareCall("BEGIN pay_mobile.sr_controller_pkg.cleanup_sr(?, ?, ?, ?, ?); END;");
                stmt.setLong("p_id$", id);
                stmt.setInt("p_lag", SRController.timeOut);
                stmt.registerOutParameter("p_result", Types.VARCHAR);
                stmt.registerOutParameter("p_status", Types.VARCHAR);
                stmt.registerOutParameter("p_src_interface", Types.VARCHAR);
                stmt.executeUpdate();
                SRController.logger.log(SRController.LOG_LEVEL_9, "SR cleanup stage 3.");
                String result = stmt.getString("p_result");
                if (result.equals("C")) {
                    String srcInterface = stmt.getString("p_src_interface");
                    byte status = stmt.getString("p_status").getBytes()[0];
                    SRController.interfaces.get(srcInterface).send(id, status, true);
                    SMPPServer.send(id);
                } else if (result.equals("I")) {
                    SMPPServer.send(id);
                } else if (result.equals("N")) {
                } else {
                    Logger.logError("SR cleanup error [" + result + "].");
                }
                stmt.close();
                stmt = null;
                SRController.logger.log(SRController.LOG_LEVEL_9, "SR cleanup stage 4.");
            } catch (IOException e) {
                Logger.logError("SR cleanup io error:", e);
            } catch (LoggerException e) {
                Logger.logError("SR cleanup log error:", e);
            } catch (SQLException e) {
                Logger.logError("SR cleanup sql error:", e);
            } finally {
                if (stmt != null) {
                    try {
                        SRController.connection.rollback();
                        stmt.close();
                    } catch (SQLException e) {
                        Logger.logError("SR cleanup close error:", e);
                    }
                }
            }
        }
    }
}

