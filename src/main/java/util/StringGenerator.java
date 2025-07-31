package main.java.util;

import java.security.SecureRandom;

public class StringGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generate a random String of length `length` with the characters `characters`
     * 
     * @param length     the length
     * @param CHARACTERS the characters
     * @return the random string
     */
    public static String generateRandomString(int length, String CHARACTERS) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
