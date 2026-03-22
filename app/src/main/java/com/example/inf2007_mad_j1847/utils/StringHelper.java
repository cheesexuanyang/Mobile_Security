package com.example.inf2007_mad_j1847.utils;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class StringHelper {

    private static final String TAG = "StringHelper";

    // =========================
    // Native/JNI section
    // =========================
    private static boolean nativeLoaded = false;

    static {
        try {
            System.loadLibrary("nativekeys");   // loads libnativekeys.so
            nativeLoaded = true;
            Log.d(TAG, "nativekeys loaded successfully");
        } catch (UnsatisfiedLinkError e) {
            nativeLoaded = false;
            Log.e(TAG, "Failed to load nativekeys", e);
        }
    }

    // JNI method implemented in C/C++
    public static native byte[] getNativePart16();

    // =========================
    // Static key former section
    // =========================

    // Java-side fixed 16-byte part
    private static final byte[] JAVA_PART16 = new byte[] {
            (byte) 0x21, (byte) 0x43, (byte) 0x65, (byte) 0x87,
            (byte) 0x10, (byte) 0x32, (byte) 0x54, (byte) 0x76,
            (byte) 0x98, (byte) 0xBA, (byte) 0xDC, (byte) 0xFE,
            (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44
    };


    private static final int[] PERM = {
            7, 19, 2, 28, 14, 0, 23, 11,
            31, 5, 16, 9, 25, 1, 20, 13,
            4, 29, 17, 8, 26, 12, 30, 6,
            21, 3, 27, 10, 18, 15, 24, 22
    };
    private static String bytesToHex(byte[] data) {
        if (data == null) return "null";

        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format(Locale.US, "%02X", b));
        }
        return sb.toString();
    }

    public static void testNativeLoad() {
        Log.d(TAG, "============================================");
        Log.d(TAG, "ENTER testNativeLoad()");

        if (!nativeLoaded) {
            Log.d(TAG, "Native library not loaded");
            Log.d(TAG, "============================================");
            return;
        }

        try {
            byte[] part = getNativePart16();
            Log.d(TAG, "getNativePart16() length: " + (part == null ? "null" : part.length));

            if (part != null) {
                Log.d(TAG, "nativePart16 hex: " + bytesToHex(part));
            }
        } catch (Throwable t) {
            Log.e(TAG, "getNativePart16() failed", t);
        }

        Log.d(TAG, "============================================");
    }

    public static byte[] formStaticKey() {
        try {
            if (!nativeLoaded) {
                Log.e(TAG, "formStaticKey() failed: native library not loaded");
                return null;
            }

            byte[] nativePart16 = getNativePart16();
            if (nativePart16 == null || nativePart16.length != 16) {
                Log.e(TAG, "formStaticKey() failed: nativePart16 is null or not 16 bytes");
                return null;
            }

            byte[] combined = new byte[32];
            System.arraycopy(JAVA_PART16, 0, combined, 0, 16);
            System.arraycopy(nativePart16, 0, combined, 16, 16);

            byte[] formedKey = new byte[32];
            for (int i = 0; i < 32; i++) {
                formedKey[i] = combined[PERM[i]];
            }

            return formedKey;

        } catch (Exception e) {
            Log.e(TAG, "formStaticKey() failed", e);
            return null;
        }
    }

    public static void testStaticKeyFormer() {
        Log.d(TAG, "============================================");
        Log.d(TAG, "ENTER testStaticKeyFormer()");

        try {
            Log.d(TAG, "javaPart16 len: " + JAVA_PART16.length);
            Log.d(TAG, "javaPart16 hex: " + bytesToHex(JAVA_PART16));

            if (nativeLoaded) {
                byte[] nativePart16 = getNativePart16();
                Log.d(TAG, "nativePart16 len: " + (nativePart16 == null ? "null" : nativePart16.length));
                Log.d(TAG, "nativePart16 hex: " + bytesToHex(nativePart16));
            } else {
                Log.d(TAG, "nativePart16: library not loaded");
            }

            byte[] formedKey = formStaticKey();
            Log.d(TAG, "formedKey len: " + (formedKey == null ? "null" : formedKey.length));
            Log.d(TAG, "formedKey hex: " + bytesToHex(formedKey));

        } catch (Exception e) {
            Log.e(TAG, "testStaticKeyFormer() failed", e);
        }

        Log.d(TAG, "============================================");
    }

    public static String encrypt(String plaintext) {
        Log.d(TAG, "ENTER encrypt(): " + plaintext);

        if (plaintext == null) {
            Log.d(TAG, "EXIT encrypt(): null");
            return null;
        }

        try {
            byte[] keyBytes = formStaticKey();
            if (keyBytes == null || keyBytes.length != 32) {
                Log.e(TAG, "encrypt() failed: formStaticKey() did not return 32 bytes");
                Log.d(TAG, "EXIT encrypt(): null");
                return null;
            }

            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] output = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, output, 0, iv.length);
            System.arraycopy(ciphertext, 0, output, iv.length, ciphertext.length);

            String encoded = Base64.encodeToString(output, Base64.NO_WRAP);
            Log.d(TAG, "EXIT encrypt(): " + encoded);
            return encoded;

        } catch (Exception e) {
            Log.e(TAG, "encrypt() failed", e);
            Log.d(TAG, "EXIT encrypt(): null");
            return null;
        }
    }

    public static String decrypt(String encryptedBase64) {
        Log.d(TAG, "ENTER decrypt(): " + encryptedBase64);

        if (encryptedBase64 == null || encryptedBase64.isEmpty()) {
            Log.d(TAG, "EXIT decrypt(): " + encryptedBase64);
            return encryptedBase64;
        }

        try {
            byte[] keyBytes = formStaticKey();
            if (keyBytes == null || keyBytes.length != 32) {
                Log.e(TAG, "decrypt() failed: formStaticKey() did not return 32 bytes");
                Log.d(TAG, "EXIT decrypt(): null");
                return null;
            }

            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

            byte[] input = Base64.decode(encryptedBase64, Base64.DEFAULT);
            if (input.length < 13) {
                Log.e(TAG, "decrypt() failed: input too short");
                Log.d(TAG, "EXIT decrypt(): null");
                return null;
            }

            byte[] iv = new byte[12];
            byte[] ciphertext = new byte[input.length - 12];

            System.arraycopy(input, 0, iv, 0, 12);
            System.arraycopy(input, 12, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] plaintext = cipher.doFinal(ciphertext);
            String result = new String(plaintext, StandardCharsets.UTF_8);

            Log.d(TAG, "EXIT decrypt(): " + result);
            return result;

        } catch (Exception e) {
            Log.e(TAG, "decrypt() failed", e);
            Log.d(TAG, "EXIT decrypt(): null");
            return null;
        }
    }

    /**
     * Main method - automatically extracts caller's filename and line number
     */
    public static String qzxp(String input) {
        Log.d(TAG, "============================================");
        Log.d(TAG, "ENTER qzxp() with input: \"" + input + "\"");

        if (input == null || input.isEmpty()) {
            Log.d(TAG, "Input is null or empty, returning as-is");
            Log.d(TAG, "RETURN: \"" + input + "\"");
            Log.d(TAG, "============================================");
            return input;
        }

        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

            if (stackTrace.length > 3) {
                StackTraceElement caller = stackTrace[3];
                String fileName = caller.getFileName();
                int lineNumber = caller.getLineNumber();

                Log.d(TAG, "File: " + fileName + ":" + lineNumber);

                String result = processInternal(input, fileName, lineNumber);

                Log.d(TAG, "RETURN: \"" + result + "\"");
                Log.d(TAG, "============================================");

                return result;
            } else {
                Log.d(TAG, "Stack trace too shallow, using fallback");
                String result = processInternal(input, "Unknown.java", 0);
                Log.d(TAG, "RETURN: \"" + result + "\"");
                Log.d(TAG, "============================================");
                return result;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in qzxp(): " + e.getMessage());
            Log.d(TAG, "RETURN (error fallback): \"" + input + "\"");
            Log.d(TAG, "============================================");
            return input;
        }
    }

    /**
     * Internal method that does the actual XOR + Base64 work
     */
    private static String processInternal(String input, String file, int line) {
        Log.d(TAG, "   processInternal()");
        Log.d(TAG, "   Input: \"" + input + "\"");
        Log.d(TAG, "   Using key: \"" + file + ":" + line + "\"");

        try {
            String keySource = file + ":" + line;
            byte[] key = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(keySource.getBytes("UTF-8"));

            boolean isBase64 = isBase64Format(input);

            byte[] data;
            if (isBase64) {
                data = Base64.decode(input, Base64.DEFAULT);
                Log.d(TAG, "   Decoded from Base64: " + data.length + " bytes");
            } else {
                data = input.getBytes("UTF-8");
                Log.d(TAG, "   Plaintext bytes: " + data.length + " bytes");
            }

            byte[] result = new byte[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = (byte) (data[i] ^ key[i % key.length]);
            }

            String output;
            if (isBase64) {
                output = new String(result, "UTF-8");
                Log.d(TAG, "   Decrypted to: \"" + output + "\"");
            } else {
                output = Base64.encodeToString(result, Base64.NO_WRAP);
                Log.d(TAG, "   Encrypted to: \"" + output + "\"");
            }

            Log.d(TAG, "   processInternal() returning");
            return output;

        } catch (Exception e) {
            Log.e(TAG, "   Error: " + e.getMessage());
            return input;
        }
    }

    /**
     * Helper method to check if string is Base64 format
     */
    private static boolean isBase64Format(String input) {
        return input.matches("^[A-Za-z0-9+/]*={0,2}$") && input.length() % 4 == 0;
    }

    /**
     * Test method
     */
    public static void testObfuscation() {
        testNativeLoad();
        testStaticKeyFormer();

        String enc = encrypt("TapTrap-admin");
        Log.d(TAG, "AES enc: " + enc);

        String dec = decrypt(enc);
        Log.d(TAG, "AES dec: " + dec);


        encrypt("Ransomware");
        encrypt("GIVE ME A+");

    }
}