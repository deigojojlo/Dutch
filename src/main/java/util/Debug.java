package main.java.util;

import java.io.UnsupportedEncodingException;

public class Debug {

    /**
     * Print a string with a byte list
     * 
     * @param payload the byte list
     */
    public static void printByteString(byte[] payload) {
        try {
            String message = new String(payload, "UTF-8");
            System.out.println("Message reçu du serveur : " + message);
            for (byte b : payload) {
                System.out.print(b + " ");
            }
            System.out.println();
        } catch (UnsupportedEncodingException e) {

        }
    }
}
