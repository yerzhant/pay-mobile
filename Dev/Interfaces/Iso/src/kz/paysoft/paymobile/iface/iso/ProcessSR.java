/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.01   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.iface.iso;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.math.BigDecimal;

import java.net.Socket;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import java.util.HashMap;

import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


final class ProcessSR extends Thread {
    private static final HashMap<Byte, String> ACCOUNT_TYPES = new HashMap<Byte, String>();
    private static final HashMap<Byte, String> AMOUNT_TYPES = new HashMap<Byte, String>();
    private static final Short PAN = 2;
    private static final Short PC = 3;
    private static final Short AMOUNT = 4;
    private static final Short RC = 39;
    private static final Short CURRENCY = 49;
    private static final Short ADD_AMOUNTS = 54;
    private static String credit, debit;
    private final long id;
    private final byte status;
    private final boolean cancel;
    private Socket socket;
    byte[] data;

    ProcessSR(long id, byte status, boolean cancel) {
        this.id = id;
        this.status = status;
        this.cancel = cancel;
    }

    public void run() {
        try {
            processRequest();
            if (socket != null) {
                processResponse();
            }
        } catch (LoggerException e) {
            Logger.logError("SR processing log error:", e);
        } catch (EngineException e) {
            Logger.logError("SR processing eg error:", e);
        } catch (ParseException e) {
            Logger.logError("SR processing ps error:", e);
        } catch (IOException e) {
            Logger.logError("SR processing io error:", e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Logger.logError("SR processing io close error:", e);
                }
            }
        }
    }

    private final void processRequest() throws LoggerException {
        Iso.logger.log(Iso.LOG_LEVEL_9, "Request processing stage 1 [" + id + "].");
        synchronized (Iso.connection) {
            CallableStatement stmt = null;
            try {
                Iso.logger.log(Iso.LOG_LEVEL_9, "Request processing stage 2 [" + id + "].");
                stmt = Iso.connection.prepareCall("BEGIN pay_mobile.interface_pkg.process_request(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); END;");
                stmt.setLong("p_id$", id);
                stmt.setString("p_interface", Iso.interfaceCode);
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
                Iso.logger.log(Iso.LOG_LEVEL_9, "Request processing stage 3 [" + id + "].");
                String result = stmt.getString("p_result");
                if (result.equals("S")) {
                    socket = Engine.send(id, stmt);
                } else if (result.equals("R")) {
                    Iso.sendNotification(id, false);
                } else if (result.equals("C")) {
                    Iso.sendNotification(id, true);
                } else {
                    throw new ProcessSRException("System error [" + result + "].");
                }
                Iso.connection.commit();
                stmt.close();
                stmt = null;
                Iso.logger.log(Iso.LOG_LEVEL_9, "Request processing stage 4 [" + id + "].");
            } catch (IOException e) {
                Logger.logError("Request processing io error:", e);
            } catch (LoggerException e) {
                Logger.logError("Request processing log error:", e);
            } catch (EngineException e) {
                Logger.logError("Request processing eg error:", e);
            } catch (ProcessSRException e) {
                Logger.logError("Request processing sr error:", e);
            } catch (SQLException e) {
                Logger.logError("Request processing sql error:", e);
            } finally {
                if (stmt != null) {
                    try {
                        Iso.connection.rollback();
                        stmt.close();
                    } catch (SQLException e) {
                        Logger.logError("Request processing close error:", e);
                    }
                }
            }
        }
    }

    private final void processResponse() throws ParseException, IOException, LoggerException, EngineException {
        Message response = Engine.receive(id, socket.getInputStream(), this);
        Iso.logger.log(Iso.LOG_LEVEL_9, "Response processing stage 1 [" + id + "].");
        synchronized (Iso.connection) {
            CallableStatement stmt = null;
            try {
                Iso.logger.log(Iso.LOG_LEVEL_9, "Response processing stage 2 [" + id + "].");
                socket.close();
                socket = null;
                stmt = Iso.connection.prepareCall("BEGIN pay_mobile.interface_pkg.process_response(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); END;");
                stmt.setLong("p_id$", id);
                stmt.setString("p_interface", Iso.interfaceCode);
                if (response.type == 0x110 || response.type == 0x210 || response.type == 0x230) {
                    stmt.setString("p_type", "R");
                } else if (response.type == 0x410 || response.type == 0x430) {
                    stmt.setString("p_type", "C");
                } else {
                    stmt.setNull("p_type", Types.VARCHAR);
                }
                stmt.setInt("p_status_detail", Engine.RC_MAPPING.get(response.getFieldValue(RC)));
                stmt.setString("p_account", response.getFieldValue(PAN));
                stmt.setNull("p_date", Types.DATE);
                boolean isBalanse = response.getFieldValue(PC).substring(0, 2).equals("30");
                if (isBalanse) {
                    stmt.setString("p_data_type", "B");
                    stmt.setNull("p_amount", Types.BIGINT);
                    stmt.setNull("p_currency", Types.VARCHAR);
                } else {
                    Engine.CurrencyInfo currInfo = Engine.ISO_CURRENCY_MAPPING.get(response.getFieldValue(CURRENCY));
                    long amount = Engine.convertAmount(Long.parseLong(response.getFieldValue(AMOUNT)), currInfo.isoPointPos, Engine.SYSTEM_POINT_POSITION);
                    stmt.setLong("p_amount", amount);
                    stmt.setString("p_currency", currInfo.currency);
                    stmt.setString("p_data_type", "M");
                }
                stmt.registerOutParameter("p_result", Types.VARCHAR);
                stmt.registerOutParameter("p_message_data", Types.BLOB);
                stmt.registerOutParameter("p_sr_data", Types.BLOB);
                stmt.executeUpdate();
                Iso.logger.log(Iso.LOG_LEVEL_9, "Response processing stage 3 [" + id + "].");
                String result = stmt.getString("p_result");
                if (result.equals("D")) {
                    Iso.sendNotification(id, false);
                    stmt.getBlob("p_message_data").setBytes(1, data);
                    if (isBalanse) {
                        stmt.getBlob("p_sr_data").setBytes(1, parseBalanse(response.getFieldValue(ADD_AMOUNTS)));
                    }
                } else if (result.equals("C")) {
                    Iso.sendNotification(id, true);
                } else {
                    throw new ProcessSRException("System error [" + result + "].");
                }
                Iso.connection.commit();
                stmt.close();
                stmt = null;
                Iso.logger.log(Iso.LOG_LEVEL_9, "Response processing stage 4 [" + id + "].");
            } catch (IOException e) {
                Logger.logError("Reponse processing io error:", e);
            } catch (LoggerException e) {
                Logger.logError("Reponse processing log error:", e);
            } catch (EngineException e) {
                Logger.logError("Reponse processing eg error:", e);
            } catch (ProcessSRException e) {
                Logger.logError("Reponse processing sr error:", e);
            } catch (SQLException e) {
                Logger.logError("Reponse processing sql error:", e);
            } finally {
                if (stmt != null) {
                    try {
                        Iso.connection.rollback();
                        stmt.close();
                    } catch (SQLException e) {
                        Logger.logError("Reponse processing close error:", e);
                    }
                }
            }
        }
    }

    private static final byte[] parseBalanse(String addAmounts) throws ProcessSRException, IOException {
        if (addAmounts.length() % 20 != 0) {
            throw new ProcessSRException("Data length is not divisable by 20 [" + addAmounts.length() + "].");
        }
        String value = "";
        for (int i = 0; i < addAmounts.length() / 20; i++) {
            String subData = addAmounts.substring(20 * i, 20 * i + 20);
            Engine.CurrencyInfo currencyInfo = Engine.ISO_CURRENCY_MAPPING.get(subData.substring(4, 7));
            value += String.format(AMOUNT_TYPES.get(Byte.parseByte(subData.substring(2, 4))));
            value += String.format(ACCOUNT_TYPES.get(Byte.parseByte(subData.substring(0, 2))));
            value += subData.substring(7, 8).equals("C") ? credit : debit;
            value += new BigDecimal(subData.substring(8)).divide(new BigDecimal(10).pow(currencyInfo.isoPointPos)).toPlainString();
            value += " " + currencyInfo.currency + "\n\n";
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(value);
        byte[] buf = baos.toByteArray();
        dos.close();
        baos.close();
        return buf;
    }

    static final void setupProcessSR(Element config) {
        credit = config.getAttribute("credit");
        debit = config.getAttribute("debit");
        NodeList amounts = config.getElementsByTagName("amount");
        for (int a = 0; a < amounts.getLength(); a++) {
            Element amount = (Element)amounts.item(a);
            Byte code = Byte.parseByte(amount.getAttribute("code"));
            String text = amount.getAttribute("text");
            AMOUNT_TYPES.put(code, text);
        }
        NodeList accounts = config.getElementsByTagName("account");
        for (int a = 0; a < accounts.getLength(); a++) {
            Element account = (Element)accounts.item(a);
            Byte code = Byte.parseByte(account.getAttribute("code"));
            String text = account.getAttribute("text");
            ACCOUNT_TYPES.put(code, text);
        }
    }
}
