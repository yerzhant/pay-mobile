/*
 * Copyright (C) 2010 PaySoft, LLP. All Rights Reserved.
 *
 * Error prefix: A.
 * Last error index: 001.
 *
 * 2010.03.27   Yerzhan Tulepov         Moved here from a test project.
 * 2010.03.10   Yerzhan Tulepov         Created.
 */
package kz.paysoft.paymobile.midlet;

/**
 * A reduced, in terms of the message length of up to 2^31-1 bits, implementation
 * of SHA-256 as per FIPS-180-3.
 *
 * @author Yerzhan Tulepov
 */
final class SHA256 {

    private static final int K[] = {
        0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
        0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
        0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
        0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };
    private static final int W[] = new int[64];
    private static int a, b, c, d, e, f, g, h, T1, T2;
    private static byte paddedData[];

    static final byte[] hash(byte[] data, boolean clearData) throws SHA256Exception {
        if (data.length == 0) {
            throw new SHA256Exception("A-001\n"); // Empty data.
        }
        paddedData = pad(data);
        //debugHex(paddedData);
        int H[] = {0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19};
        for (int i = 1; i <= (paddedData.length / 64); i++) {
            for (int t = 0; t < 64; t++) {
                if (t < 16) {
                    W[t] = getInt(paddedData, i - 1, t);
                } else {
                    W[t] = epsilon1(W[t - 2]) + W[t - 7] + epsilon0(W[t - 15]) + W[t - 16];
                }
            }
            a = H[0];
            b = H[1];
            c = H[2];
            d = H[3];
            e = H[4];
            f = H[5];
            g = H[6];
            h = H[7];
            for (int t = 0; t < 64; t++) {
                T1 = h + epsilonCapital1(e) + ch(e, f, g) + K[t] + W[t];
                T2 = epsilonCapital0(a) + maj(a, b, c);
                h = g;
                g = f;
                f = e;
                e = d + T1;
                d = c;
                c = b;
                b = a;
                a = T1 + T2;
                /*System.out.print(String.format("%02d: ", t));
                System.out.print(Integer.toHexString(a));
                System.out.println();*/
            }
            H[0] += a;
            H[1] += b;
            H[2] += c;
            H[3] += d;
            H[4] += e;
            H[5] += f;
            H[6] += g;
            H[7] += h;
        }
        byte out[] = new byte[32];
        for (int i = 0; i < 8; i++) {
            out[i * 4] = (byte) (H[i] >>> 24);
            out[i * 4 + 1] = (byte) (H[i] >>> 16);
            out[i * 4 + 2] = (byte) (H[i] >>> 8);
            out[i * 4 + 3] = (byte) H[i];
            H[i] = 0;
        }
        if (clearData) {
            PayMobile.clearBuffer(data);
        }
        cleanUp();
        return out;
    }

    static final void cleanUp() {
        PayMobile.clearBuffer(paddedData);
        paddedData = null;
        for (int t = 0; t < 64; t++) {
            W[t] = 0;
        }
        a = 0;
        b = 0;
        c = 0;
        d = 0;
        e = 0;
        f = 0;
        g = 0;
        h = 0;
        T1 = 0;
        T2 = 0;
    }

    private static final byte[] pad(byte[] data) {
        int length = 64 - (data.length + 8) % 64;
        paddedData = new byte[data.length + length + 8];
        //System.arraycopy(data, 0, paddedData, 0, data.length); This may provide perfomance benfits over the next 3 lines of code.
        for (int i = 0; i < data.length; i++) {
            paddedData[i] = data[i];
        }
        paddedData[data.length] = (byte) 0x80;
        int dataLength = data.length * 8;
        paddedData[data.length + length + 4] = (byte) (dataLength >>> 24);
        paddedData[data.length + length + 5] = (byte) (dataLength >>> 16);
        paddedData[data.length + length + 6] = (byte) (dataLength >>> 8);
        paddedData[data.length + length + 7] = (byte) dataLength;
        return paddedData;
    }

    private static final int ch(int x, int y, int z) {
        return (x & y) ^ (~x & z);
    }

    private static final int maj(int x, int y, int z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    private static final int rotr(int x, int n) {
        return (x >>> n) | (x << (32 - n));
    }

    private static final int shr(int x, int n) {
        return x >>> n;
    }

    private static final int epsilonCapital0(int x) {
        return rotr(x, 2) ^ rotr(x, 13) ^ rotr(x, 22);
    }

    private static final int epsilonCapital1(int x) {
        return rotr(x, 6) ^ rotr(x, 11) ^ rotr(x, 25);
    }

    private static final int epsilon0(int x) {
        return rotr(x, 7) ^ rotr(x, 18) ^ shr(x, 3);
    }

    private static final int epsilon1(int x) {
        return rotr(x, 17) ^ rotr(x, 19) ^ shr(x, 10);
    }

    private static final int getInt(byte[] paddedData, int n, int t) {
        int x = n * 64 + t * 4;
        int i = (paddedData[x] << 24) & 0xff000000;
        i |= (paddedData[x + 1] << 16) & 0xff0000;
        i |= (paddedData[x + 2] << 8) & 0xff00;
        i |= paddedData[x + 3] & 0xff;
        return i;
    }
    // ONLY FOR DEBUGGING!!!
    /*private static void debugHex(byte[] data) {
    System.out.print(data.length + ":");
    for (int i = 0; i < data.length; i++) {
    if ((i % 4) == 0) {
    System.out.print(" ");
    }
    System.out.print(String.format("%02x", data[i]));
    }
    System.out.println();
    }*/
}
