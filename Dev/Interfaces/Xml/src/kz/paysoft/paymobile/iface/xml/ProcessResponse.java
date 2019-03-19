/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.03   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.iface.xml;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;

import org.w3c.dom.Element;


final class ProcessResponse extends Thread {
    private final Element response;
    private final byte[] buffer;

    ProcessResponse(Element response, byte[] buffer) throws ProcessException {
        if (!response.getTagName().equals("response")) {
            throw new ProcessException("The received message is not a response.");
        }
        this.response = response;
        this.buffer = buffer;
    }

    public void run() {
        long id = Long.parseLong(response.getAttribute("id"));
        try {
            Xml.logger.log(Xml.LOG_LEVEL_2, "Received " + buffer.length + " bytes [" + id + "]:", buffer);
            Xml.logger.log(Xml.LOG_LEVEL_9, "Response processing stage 1 [" + id + "].");
        } catch (LoggerException e) {
            Logger.logError("Response processing log error:", e);
            return;
        }
        synchronized (Xml.connection) {
            CallableStatement stmt = null;
            try {
                Xml.logger.log(Xml.LOG_LEVEL_9, "Response processing stage 2 [" + id + "].");
                stmt = Xml.connection.prepareCall("BEGIN pay_mobile.interface_pkg.process_response(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); END;");
                stmt.setLong("p_id$", id);
                stmt.setString("p_interface", Xml.interfaceCode);
                if (response.getAttribute("type").equals("Cancel")) {
                    stmt.setString("p_type", "C");
                } else {
                    stmt.setString("p_type", "R");
                }
                stmt.setInt("p_status_detail", Integer.parseInt(response.getAttribute("code")));
                stmt.setString("p_date", response.getAttribute("date"));
                stmt.setString("p_account", response.getAttribute("account"));
                String data = response.getAttribute("data");
                if (data != null) {
                    stmt.setString("p_data_type", "B");
                    stmt.setNull("p_amount", Types.BIGINT);
                    stmt.setNull("p_currency", Types.VARCHAR);
                } else {
                    stmt.setString("p_data_type", "M");
                    stmt.setLong("p_amount", Long.parseLong(response.getAttribute("amount")) * 100);
                    stmt.setString("p_currency", response.getAttribute("currency"));
                }
                stmt.registerOutParameter("p_result", Types.VARCHAR);
                stmt.registerOutParameter("p_message_data", Types.BLOB);
                stmt.registerOutParameter("p_sr_data", Types.BLOB);
                stmt.executeUpdate();
                Xml.logger.log(Xml.LOG_LEVEL_9, "Response processing stage 3 [" + id + "].");
                String result = stmt.getString("p_result");
                if (result.equals("D")) {
                    Xml.sendNotification(id, false);
                    stmt.getBlob("p_message_data").setBytes(1, buffer);
                    if (data != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(baos);
                        out.writeUTF(data);
                        stmt.getBlob("p_sr_data").setBytes(1, baos.toByteArray());
                        out.close();
                        baos.close();
                    }
                } else if (result.equals("C")) {
                    Xml.sendNotification(id, true);
                } else {
                    throw new ProcessException("System error [" + result + "].");
                }
                Xml.connection.commit();
                stmt.close();
                stmt = null;
                Xml.logger.log(Xml.LOG_LEVEL_9, "Response processing stage 4 [" + id + "].");
            } catch (IOException e) {
                Logger.logError("Response processing io error:", e);
            } catch (LoggerException e) {
                Logger.logError("Response processing log error:", e);
            } catch (ProcessException e) {
                Logger.logError("Response processing error:", e);
            } catch (SQLException e) {
                Logger.logError("Response processing sql error:", e);
            } finally {
                if (stmt != null) {
                    try {
                        Xml.connection.rollback();
                        stmt.close();
                    } catch (SQLException e) {
                        Logger.logError("Response processing close error:", e);
                    }
                }
            }
        }
    }
}
