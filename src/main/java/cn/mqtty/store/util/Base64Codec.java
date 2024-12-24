package cn.mqtty.store.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Codec {
    /**
     * Decodes a Base64 encoded string.
     *
     * @param base64String The Base64 encoded string.
     * @return The decoded string.
     */
    public static String decode(String base64String) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return "Invalid Base64 input: " + e.getMessage();
        }
    }

    /**
     * Encodes a string to Base64.
     *
     * @param plainText The plain text string.
     * @return The Base64 encoded string.
     */
    public static String encode(String plainText) {
        try {
            byte[] encodedBytes = Base64.getEncoder().encode(plainText.getBytes(StandardCharsets.UTF_8));
            return new String(encodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Encoding failed: " + e.getMessage();
        }
    }

}
