/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.25   Yerzhan Tulepov         Created.
 */
//P-002

package kz.paysoft.paymobile.core.srcontroller;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import kz.paysoft.common.decryptator.Decryptator;
import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;


final class ProcessSR extends Thread {
    private static final short SAI_1_L = 0x100;
    private static final byte[] SAI_1_MKHMS01 = { -78, -76, -73, -78, -84, -49, -50 }; // MKHMS01.
    private static final byte[] SAI_1_SKHMS01 = { -84, -76, -73, -78, -84, -49, -50 }; // SKHMS01.
    private static final byte[] SAI_1_HMAC_SHA_256 = { -73, -110, -98, -100, -84, -73, -66, -51, -54, -55 }; // HmacSHA256.
    private static final byte SAI_1 = 1;
    private final long id;
    static final ConcurrentHashMap<Byte, SecretKey> rootMasterKeys = new ConcurrentHashMap<Byte, SecretKey>();
    static int processorId;

    ProcessSR(long id) {
        this.id = id;
    }

    public void run() {
        CallableStatement stmt = null;
        try {
            SRController.logger.log(SRController.LOG_LEVEL_9, "SR processing stage 1 [" + id + "].");
        } catch (LoggerException e) {
            Logger.logError("SR cleanup log error:", e);
        }
        synchronized (SRController.connection) {
            try {
                SRController.logger.log(SRController.LOG_LEVEL_9, "SR processing stage 2 [" + id + "].");
                stmt = SRController.connection.prepareCall("BEGIN pay_mobile.sr_controller_pkg.pre_process_sr(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); END;");
                stmt.setLong("p_id$", id);
                stmt.registerOutParameter("p_result", Types.VARCHAR);
                stmt.registerOutParameter("p_phone_number", Types.BIGINT);
                stmt.registerOutParameter("p_csi", Types.BIGINT);
                stmt.registerOutParameter("p_type_code", Types.VARCHAR);
                stmt.registerOutParameter("p_rmki", Types.INTEGER);
                stmt.registerOutParameter("p_sai", Types.INTEGER);
                stmt.registerOutParameter("p_sc", Types.INTEGER);
                stmt.registerOutParameter("p_src_name", Types.VARCHAR);
                stmt.registerOutParameter("p_dst_name", Types.VARCHAR);
                stmt.registerOutParameter("p_amount", Types.BIGINT);
                stmt.registerOutParameter("p_currency", Types.VARCHAR);
                stmt.registerOutParameter("p_ptc", Types.VARCHAR);
                stmt.registerOutParameter("p_description", Types.VARCHAR);
                stmt.registerOutParameter("p_st_type", Types.INTEGER);
                stmt.registerOutParameter("p_rn", Types.BLOB);
                stmt.registerOutParameter("p_sn", Types.BLOB);
                stmt.registerOutParameter("p_dst_interface", Types.VARCHAR);
                stmt.registerOutParameter("p_sr_status", Types.VARCHAR);
                stmt.registerOutParameter("p_check_fields", Types.VARCHAR);
                stmt.registerOutParameter("p_is_2_phase", Types.VARCHAR);
                stmt.registerOutParameter("p_applied_at", Types.VARCHAR);
                stmt.executeUpdate();
                SRController.logger.log(SRController.LOG_LEVEL_9, "SR processing stage 3 [" + id + "].");
                String result = stmt.getString("p_result");
                String is2phase = stmt.getString("p_is_2_phase");
                if (result.equals("S")) {
                    postProcess(verifySignature(stmt), is2phase);
                } else if (result.equals("D")) {
                    String dstInterface = stmt.getString("p_dst_interface");
                    byte status = stmt.getString("p_sr_status").getBytes()[0];
                    SRController.interfaces.get(dstInterface).send(id, status, false);
                } else if (result.equals("C")) {
                    SRController.enqueue(id, true);
                } else if (result.equals("X")) {
                    SMPPServer.send(id);
                    if (is2phase != null && is2phase.equals("Y")) {
                        FrontServer.send(id);
                    }
                } else {
                    Logger.logError("SR pre-processing error [" + result + "].");
                }
                stmt.close();
                stmt = null;
                SRController.logger.log(SRController.LOG_LEVEL_9, "SR processing stage 4 [" + id + "].");
            } catch (InterruptedException e) {
                Logger.logError("SR processing int error:", e);
            } catch (IOException e) {
                Logger.logError("SR processing io error:", e);
            } catch (LoggerException e) {
                Logger.logError("SR processing log error:", e);
            } catch (SQLException e) {
                Logger.logError("SR processing sql error:", e);
            } catch (NoSuchAlgorithmException e) {
                Logger.logError("SR processing nsa error:", e);
            } catch (NoSuchProviderException e) {
                Logger.logError("SR processing nsp error:", e);
            } catch (InvalidKeyException e) {
                Logger.logError("SR processing key error:", e);
            } catch (ProcessSRException e) {
                Logger.logError("SR processing sr error:", e);
            } finally {
                if (stmt != null) {
                    try {
                        SRController.connection.rollback();
                        stmt.close();
                    } catch (SQLException e) {
                        Logger.logError("SR processing close error:", e);
                    }
                }
            }
        }
    }

    private final String verifySignature(CallableStatement stmt) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, ProcessSRException,
                                                                        LoggerException {
        int signatureCounter = stmt.getInt("p_sc");
        long phoneNumber = stmt.getLong("p_phone_number");
        long csi = stmt.getLong("p_csi");
        int rmki = stmt.getInt("p_rmki");
        int sai = stmt.getInt("p_sai");
        byte[] randomNumber = stmt.getBlob("p_rn").getBytes(1, (int)stmt.getBlob("p_rn").length());
        byte[] srSignature = stmt.getBlob("p_sn").getBytes(1, (int)stmt.getBlob("p_sn").length());
        byte[] appliedAt = stmt.getString("p_applied_at").getBytes();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        out.write(randomNumber);
        out.writeShort(signatureCounter);
        String check_fields = stmt.getString("p_check_fields");
        for (int i = 0; check_fields != null && i < check_fields.length(); i++) {
            String code = check_fields.substring(i, i + 1);
            if (code.equals("A")) {
                out.writeLong(stmt.getLong("p_amount"));
                out.writeUTF(stmt.getString("p_currency"));
            } else if (code.equals("P")) {
                String paymentTypeCode = stmt.getString("p_ptc");
                out.writeUTF(paymentTypeCode == null ? "" : paymentTypeCode);
            } else if (code.equals("E")) {
                String description = stmt.getString("p_description");
                out.writeUTF(description == null ? "" : description);
            } else if (code.equals("S")) {
                out.writeUTF(stmt.getString("p_src_name"));
            } else if (code.equals("D")) {
                out.writeUTF(stmt.getString("p_dst_name"));
            } else if (code.equals("T")) {
                out.writeByte(stmt.getInt("p_st_type"));
            } else {
                Decryptator.clearBuffer(randomNumber);
                Decryptator.clearBuffer(srSignature);
                throw new ProcessSRException("P-002.");
            }
        }
        out.writeByte(stmt.getInt("p_type_code"));
        out.writeLong(phoneNumber);
        out.writeLong(csi);
        out.writeByte(rmki);
        out.writeByte(sai);
        out.writeShort(processorId);
        byte[] signingData = baos.toByteArray();
        out.close();
        baos.close();

        SRController.logger.log(SRController.LOG_LEVEL_9, "Checking stage 1 [" + id + "].");
        byte[] signature = sign(sai, rmki, phoneNumber, csi, appliedAt, randomNumber, signatureCounter, signingData);
        Decryptator.clearBuffer(randomNumber);
        Decryptator.clearBuffer(signingData);
        if (srSignature.length != signature.length) {
            Decryptator.clearBuffer(srSignature);
            Decryptator.clearBuffer(signature);
            return "F"; // Failed.
        } else {
            for (int i = 0; i < signature.length; i++) {
                if (signature[i] != srSignature[i]) {
                    Decryptator.clearBuffer(srSignature);
                    Decryptator.clearBuffer(signature);
                    return "F"; // Failed.
                }
            }
            Decryptator.clearBuffer(srSignature);
            Decryptator.clearBuffer(signature);
            return "O"; // Ok.
        }
    }

    private final byte[] sign(int sai, int rmki, long phoneNumber, long csi, byte[] appliedAt, byte[] randomNumber, int signatureCounter, byte[] signingData) throws NoSuchAlgorithmException,
                                                                                                                                                                     NoSuchProviderException,
                                                                                                                                                                     InvalidKeyException, IOException,
                                                                                                                                                                     ProcessSRException,
                                                                                                                                                                     LoggerException {
        switch (sai) {
        case SAI_1:
            Mac prf;
            if (SRController.DEVELOPMENT) {
                prf = Mac.getInstance(Decryptator.toString(SAI_1_HMAC_SHA_256));
            } else {
                prf = Mac.getInstance(Decryptator.toString(SAI_1_HMAC_SHA_256), SRController.provider);
            }
            prf.init(rootMasterKeys.get(Byte.valueOf((byte)rmki)));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.write(Decryptator.toString(SAI_1_MKHMS01).getBytes());
            out.writeLong(phoneNumber);
            out.writeLong(csi);
            out.write(appliedAt);
            out.writeByte(rmki);
            out.writeShort(processorId);
            out.writeShort(SAI_1_L);
            byte[] data = baos.toByteArray();
            out.close();
            baos.close();
            byte[] masterKey = prf.doFinal(data);
//            System.out.print("MK: ");
//            printHex(masterKey);
            Decryptator.clearBuffer(data);
            SRController.logger.log(SRController.LOG_LEVEL_9, "Checking stage 2 [" + id + "].");

            prf = Mac.getInstance(Decryptator.toString(SAI_1_HMAC_SHA_256), "SunJCE");
            prf.init(new SecretKeySpec(masterKey, ""));
            baos = new ByteArrayOutputStream();
            out = new DataOutputStream(baos);
            out.write(Decryptator.toString(SAI_1_SKHMS01).getBytes());
            out.write(randomNumber);
            out.writeShort(signatureCounter);
            out.writeLong(phoneNumber);
            out.writeLong(csi);
            out.writeByte(rmki);
            out.writeByte(sai);
            out.writeShort(processorId);
            out.writeShort(SAI_1_L);
            data = baos.toByteArray();
            out.close();
            baos.close();
//            System.out.print("SK Data: ");
//            printHex(data);
            byte[] signingKey = prf.doFinal(data);
//            System.out.print("SK: ");
//            printHex(signingKey);
            Decryptator.clearBuffer(masterKey);
            Decryptator.clearBuffer(data);
            SRController.logger.log(SRController.LOG_LEVEL_9, "Checking stage 3 [" + id + "].");
            prf.init(new SecretKeySpec(signingKey, ""));
//            System.out.print("Data: ");
//            printHex(signingData);
            byte[] signature = prf.doFinal(signingData);
//            System.out.print("SG: ");
//            printHex(signature);
            Decryptator.clearBuffer(signingKey);
            SRController.logger.log(SRController.LOG_LEVEL_9, "Checking stage 4 [" + id + "].");
            return signature;

        default:
            throw new ProcessSRException("P-001.");
        }
    }

    private final void postProcess(String signatureResult, String is2phase) throws SQLException, IOException, LoggerException {
        CallableStatement stmt = null;
        try {
            stmt = SRController.connection.prepareCall("BEGIN pay_mobile.sr_controller_pkg.post_process_sr(?, ?, ?, ?); END;");
            stmt.setLong("p_id$", id);
            stmt.setString("p_result", signatureResult);
            stmt.registerOutParameter("p_result", Types.VARCHAR);
            stmt.registerOutParameter("p_src_interface", Types.VARCHAR);
            stmt.registerOutParameter("p_sr_status", Types.VARCHAR);
            stmt.executeUpdate();
            SRController.logger.log(SRController.LOG_LEVEL_9, "Post-processing stage 1 [" + id + "].");
            String result = stmt.getString("p_result");
            if (result.equals("S")) {
                String srcInterface = stmt.getString("p_src_interface");
                byte status = stmt.getString("p_sr_status").getBytes()[0];
                SRController.interfaces.get(srcInterface).send(id, status, false);
            } else if (result.equals("X")) {
                SMPPServer.send(id);
                if (is2phase != null && is2phase.equals("Y")) {
                    FrontServer.send(id);
                }
            } else {
                Logger.logError("SR post-processing error [" + result + "].");
            }
            stmt.close();
            stmt = null;
            SRController.logger.log(SRController.LOG_LEVEL_9, "Post-processing stage 2 [" + id + "].");
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

//    private final void printHex(byte[] data) {
//        for (int i = 0; i < data.length; i++) {
//            System.out.printf("%02X", data[i]);
//        }
//        System.out.println();
//    }
}
