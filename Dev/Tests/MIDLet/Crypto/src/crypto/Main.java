/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crypto;

/**
 *
 * @author ADevUser
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SHA256Exception, HMACException, PBEException {
//        for (int x = 0; x < 200; x++) {
//        int y = 64 - (x + 8) % 64;
//        System.out.println(x + ", " + y + ", " + (x + y + 8));
//        }
//        int n = 1000000;
//        byte data[] = new byte[n];
//        for (int x = 0; x < data.length; x++) {
//            //data[x] = 'U';
//        }
        byte data[] = "abc".getBytes();//{(byte)0xc9, (byte)0x8c, (byte)0x8e, (byte)0x55};
        byte hash[] = SHA256.hash(data);
//
//        byte key[] = new byte[100];
//        for (int x = 0; x < key.length; x++) {
//            key[x] = (byte)x;
//        }
//        byte data[] = "Sample message for keylen<blocklen, with truncated tag".getBytes();
//        byte hash[] = HMAC.hmac(1, key, data);

//        byte hash[] = PBE.pbe(1, "password".getBytes(), "salt".getBytes(), 1024);

        for (int i = 0; i < hash.length; i++) {
            if (i != 0 && (i % 4) == 0) {
                System.out.print(" ");
            }
            System.out.print(String.format("%02X", hash[i]));
        }
        System.out.println();
    }
}
