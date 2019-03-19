/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.28   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.srcontroller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;


final class Cleaner extends Thread {
    public void run() {
        while (true) {
            if (SMPPServer.connected) {
                Statement stmt = null;
                ResultSet rset = null;
                synchronized (SRController.connection) {
                    try {
                        stmt = SRController.connection.createStatement();
                        rset = stmt.executeQuery("SELECT id$ FROM pay_mobile.v_timed_out_srs WHERE send_ntf = 'Y' OR delta > " + SRController.timeOut);
                        while (SMPPServer.connected && rset.next()) {
                            SRController.enqueue(rset.getLong("id$"), true);
                        }
                    } catch (SQLException e) {
                        Logger.logError("Cleaner sql error:", e);
                    } catch (InterruptedException e) {
                        Logger.logError("Cleaner int error:", e);
                    } finally {
                        try {
                            if (rset != null) {
                                rset.close();
                            }
                            if (stmt != null) {
                                stmt.close();
                            }
                        } catch (SQLException e) {
                            Logger.logError("Cleaner close error:", e);
                        }
                    }
                }
            }
            try {
                sleep(SRController.refreshInterval * 1000);
            } catch (InterruptedException e) {
                Logger.logError("Cleaner int error:", e);
            }
        }
    }
}
