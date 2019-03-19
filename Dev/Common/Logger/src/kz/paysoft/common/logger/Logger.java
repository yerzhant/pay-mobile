/*
 * Copyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.
 *
 * Provides logging to a standard error stream and a file.
 *
 * 2010.04.12   Yerzhan Tulepov         Migrated from PaySysLib and renamed.
 * 22.10.2008	1.1.0	Yerzhan Tulepov	1.0.0.0->1.1.0.
 * 03.05.2008	1.0.0.0	Yerzhan Tulepov	Created.
 */

package kz.paysoft.common.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;


public final class Logger {
    private static final String LINE_DELIMITER = "********************************************************************************";
    private static final String DEFAULT_DELIMITER = " | ";
    private static final String LOG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String FILE_RENAME_DATE_FORMAT = "-yyyy-MM-dd-HH-mm-ss-SSS";
    private static final int MIN_HISTORY_SIZE = 64 * 1024;
    private static String delimiter = DEFAULT_DELIMITER;
    private FileWriter fileWriter;
    private String fileName;
    private long fileSize;
    private int historySize;
    private int level, bytesPerLine = 16;

    public Logger(String level, String fileName, String historySize) throws LoggerException {
        this(level, fileName, historySize, DEFAULT_DELIMITER);
    }

    public Logger(String level, String fileName, String historySize, String delimiter) throws LoggerException {
        this.delimiter = delimiter;
        this.level = Integer.parseInt(level);
        if (this.level < 0) {
            throw new LoggerException("Log level must not be negative.");
        }
        this.fileName = fileName;
        File file = new File(this.fileName);
        fileSize = file.length();
        try {
            fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            throw new LoggerException(e);
        }
        this.historySize = Integer.parseInt(historySize);
        if (this.historySize < MIN_HISTORY_SIZE) {
            throw new LoggerException("History file size must not be less that " + MIN_HISTORY_SIZE + ".");
        }
    }

    public final synchronized void close() throws LoggerException {
        try {
            if (fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
                fileWriter = null;
            }
        } catch (IOException e) {
            throw new LoggerException(e);
        }
    }

    public final synchronized void setBytesPerLine(int bytesPerLine) {
        this.bytesPerLine = bytesPerLine;
    }

    //    public static final byte[] toBuffer(byte lenSize, int length, InputStream in) throws IOException {
    //        byte[] buf = new byte[lenSize + length];
    //        for (byte i = 0; i < lenSize; i++) {
    //            buf[i] = (byte)(length >> (lenSize - i - 1) * 8);
    //        }
    //        in.mark(length);
    //        in.read(buf, lenSize, length);
    //        in.reset();
    //        return buf;
    //    }

    //    public static final byte[] toBuffer(byte lenSize, byte[] data) throws IOException {
    //        int length = data.length;
    //        byte[] buf = new byte[lenSize + length];
    //        for (byte i = 0; i < lenSize; i++) {
    //            buf[i] = (byte)(length >> (lenSize - i - 1) * 8);
    //        }
    //        System.arraycopy(data, 0, buf, lenSize, length);
    //        return buf;
    //    }

    //    public final void log(int level, String header, int length, InputStream in) throws IOException, LoggerException {
    //        if (this.level >= level) {
    //            byte[] buf = new byte[length];
    //            in.mark(length);
    //            in.read(buf, 0, length);
    //            in.reset();
    //            log(level, header, buf);
    //        }
    //    }

    public final void log(int level, String header, byte[] data) throws LoggerException {
        if (this.level >= level) {
            synchronized (this) {
                if (header != null) {
                    log(level, header);
                }
                for (int i = 0; i <= data.length / bytesPerLine; i++) {
                    byte buf[];
                    if (data.length - i * bytesPerLine >= bytesPerLine) {
                        buf = new byte[bytesPerLine];
                    } else {
                        if (data.length - i * bytesPerLine == 0) {
                            break;
                        }
                        buf = new byte[data.length - i * bytesPerLine];
                    }
                    System.arraycopy(data, i * bytesPerLine, buf, 0, buf.length);
                    StringBuffer string = new StringBuffer();
                    for (int j = 0; j < bytesPerLine; j++) {
                        if (j < buf.length) {
                            string.append(String.format("%02X ", buf[j]));
                        } else {
                            string.append("   ");
                        }
                    }
                    string.append("\t");
                    for (int j = 0; j < buf.length; j++) {
                        if (Character.isISOControl(buf[j])) {
                            string.append(".");
                        } else {
                            string.append((char)buf[j]);
                        }
                    }
                    log(level, string.toString());
                }
            }
        }
    }

    public final void log(int level, String string) throws LoggerException {
        if (this.level >= level) {
            synchronized (this) {
                String message = new SimpleDateFormat(LOG_DATE_FORMAT).format(new Date()) + delimiter + string + "\n";

                if ((fileSize + message.length()) > historySize) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        throw new LoggerException(e);
                    }
                    File file = new File(fileName);
                    String newFileName = fileName + new SimpleDateFormat(FILE_RENAME_DATE_FORMAT).format(new Date());
                    if (!file.renameTo(new File(newFileName)))
                        throw new LoggerException("Unable to rename log file from " + fileName + " to " + newFileName);
                    try {
                        fileWriter = new FileWriter(fileName, true);
                    } catch (IOException e) {
                        throw new LoggerException(e);
                    }
                    fileSize = 0;
                }

                try {
                    fileWriter.write(message);
                    fileWriter.flush();
                } catch (IOException e) {
                    throw new LoggerException(e);
                }
                fileSize += message.length();
            }
        }
    }

    public static final synchronized void logError(String message, Exception exception) {
        System.err.println(new SimpleDateFormat(LOG_DATE_FORMAT).format(new Date()) + delimiter + message);
        exception.printStackTrace();
        System.err.println(LINE_DELIMITER);
    }

    public static final synchronized void logError(String message) {
        System.err.println(new SimpleDateFormat(LOG_DATE_FORMAT).format(new Date()) + delimiter + message);
        System.err.println(LINE_DELIMITER);
    }
}
