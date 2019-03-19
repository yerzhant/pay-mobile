/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.02   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.iface.xml;

import java.io.IOException;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;


final class ProcessRequest extends Thread {
    private final long id;
    private final byte status;
    private final boolean cancel;

    ProcessRequest(long id, byte status, boolean cancel) throws ProcessException {
        this.id = id;
        this.status = status;
        this.cancel = cancel;
    }

    public void run() {
        try {
            Xml.logger.log(Xml.LOG_LEVEL_9, "Request processing stage 1 [" + id + "].");
        } catch (LoggerException e) {
            Logger.logError("Request processing log error:", e);
            return;
        }
        synchronized (Xml.connection) {
            CallableStatement stmt = null;
            try {
                Xml.logger.log(Xml.LOG_LEVEL_9, "Request processing stage 2 [" + id + "].");
                stmt = Xml.connection.prepareCall("BEGIN pay_mobile.interface_pkg.process_request(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); END;");
                stmt.setLong("p_id$", id);
                stmt.setString("p_interface", Xml.interfaceCode);
                stmt.setString("p_status", String.valueOf((char)status));
                stmt.setString("p_cancel", cancel == true ? "Y" : "N");
                stmt.setString("p_message", "Y");
                stmt.registerOutParameter("p_result", Types.VARCHAR);
                stmt.registerOutParameter("p_contract_id$", Types.BIGINT);
                stmt.registerOutParameter("p_code", Types.VARCHAR);
                stmt.registerOutParameter("p_data", Types.BLOB);
                stmt.registerOutParameter("p_date", Types.VARCHAR);
                stmt.registerOutParameter("p_account", Types.VARCHAR);
                stmt.registerOutParameter("p_amount", Types.BIGINT);
                stmt.registerOutParameter("p_currency", Types.VARCHAR);
                stmt.registerOutParameter("p_ptc", Types.VARCHAR);
                stmt.registerOutParameter("p_description", Types.VARCHAR);
                stmt.registerOutParameter("p_st_type", Types.INTEGER);
                stmt.executeUpdate();
                Xml.logger.log(Xml.LOG_LEVEL_9, "Request processing stage 3 [" + id + "].");
                String result = stmt.getString("p_result");
                if (result.equals("S")) {
                    Xml.sendRequest(id, stmt);
                } else if (result.equals("R")) {
                    Xml.sendNotification(id, false);
                } else if (result.equals("C")) {
                    Xml.sendNotification(id, true);
                } else {
                    throw new ProcessException("System error [" + result + "].");
                }
                Xml.connection.commit();
                stmt.close();
                stmt = null;
                Xml.logger.log(Xml.LOG_LEVEL_9, "Request processing stage 4 [" + id + "].");
            } catch (IOException e) {
                Logger.logError("Request processing io error:", e);
            } catch (LoggerException e) {
                Logger.logError("Request processing log error:", e);
            } catch (ProcessException e) {
                Logger.logError("Request processing error:", e);
            } catch (SQLException e) {
                Logger.logError("Request processing sql error:", e);
            } catch (TransformerConfigurationException e) {
                Logger.logError("Request processing tfc error:", e);
            } catch (TransformerException e) {
                Logger.logError("Request processing tfm error:", e);
            } finally {
                if (stmt != null) {
                    try {
                        Xml.connection.rollback();
                        stmt.close();
                    } catch (SQLException e) {
                        Logger.logError("Request processing close error:", e);
                    }
                }
            }
        }
    }
}
