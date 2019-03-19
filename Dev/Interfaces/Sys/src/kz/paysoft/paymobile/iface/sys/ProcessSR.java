/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.29   Yerzhan Tulepov         Created.
 */
//P-001.

package kz.paysoft.paymobile.iface.sys;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;


final class ProcessSR extends Thread {
    private final long id;
    private final byte status;
    private byte[] data;

    ProcessSR(long id, byte status, boolean cancel) throws ProcessSRException {
        if (cancel) {
            throw new ProcessSRException("P-001.");
        }
        this.id = id;
        this.status = status;
    }

    public void run() {
        try {
            Sys.logger.log(Sys.LOG_LEVEL_9, "Processing stage 1 [" + id + "].");
        } catch (LoggerException e) {
            Logger.logError("Request processing log error:", e);
            return;
        }
        synchronized (Sys.connection) {
            CallableStatement stmt = null;
            try {
                Sys.logger.log(Sys.LOG_LEVEL_9, "Processing stage 2 [" + id + "].");
                stmt = Sys.connection.prepareCall("BEGIN pay_mobile.interface_pkg.process_request(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); END;");
                stmt.setLong("p_id$", id);
                stmt.setString("p_interface", Sys.interfaceCode);
                stmt.setString("p_status", String.valueOf((char)status));
                stmt.setString("p_cancel", "N");
                stmt.setString("p_message", "N");
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
                Sys.logger.log(Sys.LOG_LEVEL_9, "Processing stage 3 [" + id + "].");
                String result = stmt.getString("p_result");
                if (result.equals("S")) {
                    processRequest(stmt);
                    processResponse(stmt);
                } else if (result.equals("R")) {
                    Sys.sendNotification(id, false);
                } else if (result.equals("C")) {
                    Sys.sendNotification(id, true);
                } else {
                    throw new ProcessSRException("SR req processing error [" + result + "].");
                }
                Sys.connection.commit();
                stmt.close();
                stmt = null;
                Sys.logger.log(Sys.LOG_LEVEL_9, "Processing stage 4 [" + id + "].");
            } catch (IOException e) {
                Logger.logError("SR process io error:", e);
            } catch (ProcessSRException e) {
                Logger.logError("SR process sr error:", e);
            } catch (LoggerException e) {
                Logger.logError("SR process log error:", e);
            } catch (SQLException e) {
                Logger.logError("SR process sql error:", e);
            } finally {
                if (stmt != null) {
                    try {
                        Sys.connection.rollback();
                        stmt.close();
                    } catch (SQLException e) {
                        Logger.logError("SR process close error:", e);
                    }
                }
            }
        }
    }

    private final void processRequest(CallableStatement reqStmt) throws SQLException, IOException, LoggerException, ProcessSRException {
        if (reqStmt.getString("p_code").equals("GAL")) { // Generate Account List.
            long contractId = reqStmt.getLong("p_contract_id$");
            Statement stmt = null;
            ResultSet rset = null;
            try {
                stmt = Sys.connection.createStatement();
                rset = stmt.executeQuery("SELECT id$ FROM pay_mobile.contracts WHERE id$ = " + contractId + " AND status$ = 'C' FOR UPDATE");
                rset.close();
                rset = stmt.executeQuery("SELECT kind, name FROM pay_mobile.accounts WHERE contract_id$ = " + contractId + " AND status$ = 'C' ORDER BY priority FOR UPDATE");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                int count = 0;
                while (rset.next()) {
                    dos.writeByte(rset.getString("kind").getBytes()[0]);
                    dos.writeUTF(rset.getString("name"));
                    count++;
                }
                data = baos.toByteArray();
                baos = new ByteArrayOutputStream();
                dos = new DataOutputStream(baos);
                dos.writeShort(count);
                dos.write(data);
                data = baos.toByteArray();
                Sys.logger.log(Sys.LOG_LEVEL_1, "Update accounts processed for [" + id + "].");
            } finally {
                if (rset != null) {
                    rset.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
        } else {
            throw new ProcessSRException("Unsupported command [" + reqStmt.getString("p_code") + "].");
        }
    }

    private final void processResponse(CallableStatement reqStmt) throws SQLException, IOException, ProcessSRException {
        CallableStatement stmt = Sys.connection.prepareCall("BEGIN pay_mobile.interface_pkg.process_response(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); END;");
        stmt.setLong("p_id$", id);
        stmt.setString("p_interface", Sys.interfaceCode);
        stmt.setString("p_type", "R");
        stmt.setNull("p_status_detail", Types.INTEGER);
        stmt.setNull("p_account", Types.VARCHAR);
        stmt.setString("p_date", reqStmt.getString("p_date"));
        stmt.setNull("p_amount", Types.BIGINT);
        stmt.setNull("p_currency", Types.VARCHAR);
        stmt.setString("p_data_type", "S");
        stmt.registerOutParameter("p_result", Types.VARCHAR);
        stmt.registerOutParameter("p_message_data", Types.BLOB);
        stmt.registerOutParameter("p_sr_data", Types.BLOB);
        stmt.executeUpdate();
        String result = stmt.getString("p_result");
        if (result.equals("D")) {
            Sys.sendNotification(id, false);
            stmt.getBlob("p_sr_data").setBytes(1, data);
        } else if (result.equals("C")) {
            Sys.sendNotification(id, true);
        } else {
            throw new ProcessSRException("SR rsp processing error [" + result + "].");
        }
    }
}
