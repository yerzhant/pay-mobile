/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * SMPP SMSCenter emulator.
 *
 * 2010.04.12 Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.smscenter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Arrays;
import java.util.Date;


final class SMSCenter implements Runnable {
    private static final int RESPONSE_MASK = 0x80000000;
    private static final int SUBMIT_SM = 4;
    private static final int DELIVER_SM = 5;
    private static final int UNBIND = 6;
    private static final int BIND_TRANSCEIVER = 9;
    private static final int ENQUIRE_LINK = 0x15;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static Thread sender;
    private static String phoneNumber;
    private static int receiveSequence, sendSequence;

    public static void main(String[] args) {
        SMSCenter smscenter = new SMSCenter();
        if (args.length != 2) {
            System.out.println("Usage: SMSCenter port phoneNumber");
            System.exit(-1);
        }

        int port = Integer.parseInt(args[0]);
        phoneNumber = args[1];

        while (true) {
            sendSequence = 1;
            ServerSocket ss = null;
            Socket sock = null;
            try {
                System.out.println("Waiting for a connection...");
                ss = new ServerSocket(port);
                sock = ss.accept();
                System.out.println("Received a connection.");
                in = new DataInputStream(sock.getInputStream());
                out = new DataOutputStream(sock.getOutputStream());
                if (getMessage() != BIND_TRANSCEIVER)
                    throw new Exception("Not a BIND_TRANSCEIVER");
                sendBindResponse();
                sendEnquireLink();
                sender = new Thread(smscenter);
                sender.start();

                boolean bound = true;
                while (bound) {
                    int command = getMessage();
                    switch (command) {
                    case UNBIND:
                        sendUnbindResponse();
                        bound = false;
                        break;

                    case SUBMIT_SM:
                        sendSubmitResponse();
                        break;

                    case DELIVER_SM | RESPONSE_MASK:
                        break;

                    case ENQUIRE_LINK | RESPONSE_MASK:
                        break;

                    default:
                        throw new Exception("Unsupported command: " + command);
                    }
                }

                sender.interrupt();
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
                if (sock != null)
                    sock.close();
                if (ss != null)
                    ss.close();
                System.out.println("Disconnected.");
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
                try {
                    if (out != null)
                        out.close();
                    if (in != null)
                        in.close();
                    if (sock != null)
                        sock.close();
                    if (ss != null)
                        ss.close();
                } catch (IOException f) {
                    System.out.println("Another exception: " + f.getMessage());
                }
            }
        }
    }

    private static int getMessage() throws IOException {
        int len = in.readInt();
        int command = in.readInt();
        int status = in.readInt();
        receiveSequence = in.readInt();
        System.out.println("Received a message:");
        System.out.println(new Date());
        System.out.printf("Header: %08X %08X %08X %08X", len, command, status, receiveSequence);
        byte buf[] = new byte[len - 16];
        in.readFully(buf);
        System.out.println(" Body: " + Arrays.toString(buf));
        return command;
    }

    private static void sendBindResponse() throws IOException {
        String systemId = "Test SMSC";
        out.writeInt(16 + systemId.length() + 1);
        out.writeInt(BIND_TRANSCEIVER | RESPONSE_MASK);
        out.writeInt(0);
        out.writeInt(receiveSequence);
        out.write(systemId.getBytes());
        out.writeByte(0);
    }

    private static void sendUnbindResponse() throws IOException {
        out.writeInt(16);
        out.writeInt(UNBIND | RESPONSE_MASK);
        out.writeInt(0);
        out.writeInt(receiveSequence);
    }

    private static void sendEnquireLink() throws IOException {
        out.writeInt(16);
        out.writeInt(ENQUIRE_LINK);
        out.writeInt(0);
        out.writeInt(sendSequence++);
    }

    private static void sendSubmitResponse() throws IOException {
        String messageId = "Message Id";
        out.writeInt(16 + messageId.length() + 1);
        out.writeInt(SUBMIT_SM | RESPONSE_MASK);
        out.writeInt(0);
        out.writeInt(receiveSequence);
        out.write(messageId.getBytes());
        out.writeByte(0);
    }

    public void run() {
        while (true) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String sms = "1" + reader.readLine();
                System.out.println("Sending: " + sms);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeInt(DELIVER_SM);
                dos.writeInt(0);
                dos.writeInt(sendSequence++);
                dos.writeByte(0);
                dos.writeByte(1);
                dos.writeByte(6);
                dos.write(phoneNumber.getBytes());
                dos.writeByte(0);
                dos.writeByte(1);
                dos.writeByte(6);
                dos.write("123456789".getBytes());
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(sms.length());
                dos.write(sms.getBytes());
                out.writeInt(4 + baos.size());
                out.write(baos.toByteArray());
                baos.close();
                dos.close();
            } catch (IOException e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }
    }
}
