// File: StringHelper.java
package com.example.inf2007_mad_j1847.utils;

import android.util.Log;
import android.util.Base64;

public class StringHelper {
    public static String process(String input, String file, int line) {
        // Your XOR + Base64 implementation
        if (input == null || input.isEmpty()) return input;

        try {
            // Generate key from filename and line
            String keySource = file + ":" + line;
            byte[] key = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(keySource.getBytes("UTF-8"));

            // Check if input is Base64 (encrypted) or plaintext
            boolean isBase64 = input.matches("^[A-Za-z0-9+/]*={0,2}$") && input.length() % 4 == 0;

            byte[] data;
            if (isBase64) {
                data = Base64.decode(input, Base64.DEFAULT);
            } else {
                data = input.getBytes("UTF-8");
            }

            // XOR
            byte[] result = new byte[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = (byte)(data[i] ^ key[i % key.length]);
            }

            if (isBase64) {
                return new String(result, "UTF-8");
            } else {
                return Base64.encodeToString(result, Base64.NO_WRAP);
            }

        } catch (Exception e) {
            return input;
        }
    }

    public static void testObfuscation() {
        Log.d("STRING_TEST", "========== TESTING ==========");

        // Test TAG
        String originalTag = "TapTrap-Admin";
        String encryptedTag = "dXc4wrM5TlnW6N3hHQ==";
        String decryptedTag = process(encryptedTag, "AppDeviceAdminReceiver.java", 15);

        String en_tag2 = process(originalTag, "AppDeviceAdminReceiver.java", 15);
        Log.d("STRING_TEST", "TAG Test:");
        Log.d("STRING_TEST", "  Original: " + originalTag);
        Log.d("STRING_TEST", "  Encrypted: " + encryptedTag);
        Log.d("STRING_TEST", "  Decrypted: " + decryptedTag);
        Log.d("STRING_TEST", "  enc 2: " + en_tag2);
        Log.d("STRING_TEST", "  MATCH: " + originalTag.equals(decryptedTag));

        // Test IP
        String originalIp = "20.2.66.175";
        String encryptedIp = "MjAuMi42Ni4xNzU=";
        String decryptedIp = process(encryptedIp, "AppDeviceAdminReceiver.java", 16);

        Log.d("STRING_TEST", "IP Test:");
        Log.d("STRING_TEST", "  Original: " + originalIp);
        Log.d("STRING_TEST", "  Encrypted: " + encryptedIp);
        Log.d("STRING_TEST", "  Decrypted: " + decryptedIp);
        Log.d("STRING_TEST", "  MATCH: " + originalIp.equals(decryptedIp));

        Log.d("STRING_TEST", "========== COMPLETE ==========");
    }
}