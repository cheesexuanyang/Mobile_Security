// File: StringHelper.java
package com.example.inf2007_mad_j1847.utils;

import android.util.Log;
import android.util.Base64;

public class StringHelper {

    private static final String TAG = "StringHelper";

    /**
     * Main method - automatically extracts caller's filename and line number
     */
    public static String qzxp(String input) {
        // LOG: Entering method with input value
        Log.d(TAG, "════════════════════════════════════════════");
        Log.d(TAG, "🔵 ENTER qzxp() with input: \"" + input + "\"");

        if (input == null || input.isEmpty()) {
            Log.d(TAG, "⚠️ Input is null or empty, returning as-is");
            Log.d(TAG, "🔴 RETURN: \"" + input + "\"");
            Log.d(TAG, "════════════════════════════════════════════");
            return input;
        }

        try {
            // Get caller information from stack trace
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

            // DEBUG: Print the entire stack trace to see indexes
            //Log.d(TAG, "📋 Stack trace depth: " + stackTrace.length);
//            for (int i = 0; i < Math.min(stackTrace.length, 5); i++) {
//                Log.d(TAG, "   [" + i + "] " + stackTrace[i].getClassName() + "." +
//                        stackTrace[i].getMethodName() + "(" + stackTrace[i].getFileName() + ":" +
//                        stackTrace[i].getLineNumber() + ")");
//            }

            // The actual caller is at index 3 for Android
            // Index 0: dalvik.system.VMStack.getThreadStackTrace()
            // Index 1: java.lang.Thread.getStackTrace()
            // Index 2: StringHelper.qzxp()  (this method)
            // Index 3: The method that called qzxp()  (THIS IS WHAT WE WANT)

            if (stackTrace.length > 3) {
                StackTraceElement caller = stackTrace[3];
                String fileName = caller.getFileName();
                int lineNumber = caller.getLineNumber();
                String methodName = caller.getMethodName();
                String className = caller.getClassName();

                //Log.d(TAG, "📁 Caller detected: " + className + "." + methodName + "()");
                Log.d(TAG, "📁 File: " + fileName + ":" + lineNumber);

                // Call the internal process method with extracted info
                String result = processInternal(input, fileName, lineNumber);

                // LOG: Return value
                Log.d(TAG, "🔴 RETURN: \"" + result + "\"");
                Log.d(TAG, "════════════════════════════════════════════");

                return result;
            } else {
                Log.d(TAG, "⚠️ Stack trace too shallow, using fallback");
                String result = processInternal(input, "Unknown.java", 0);
                Log.d(TAG, "🔴 RETURN: \"" + result + "\"");
                Log.d(TAG, "════════════════════════════════════════════");
                return result;
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error in qzxp(): " + e.getMessage());
            Log.d(TAG, "🔴 RETURN (error fallback): \"" + input + "\"");
            Log.d(TAG, "════════════════════════════════════════════");
            return input;
        }
    }

    /**
     * Internal method that does the actual XOR + Base64 work
     */
    private static String processInternal(String input, String file, int line) {
        Log.d(TAG, "   ┌─ processInternal()");
        Log.d(TAG, "   │  Input: \"" + input + "\"");
        Log.d(TAG, "   │  Using key: \"" + file + ":" + line + "\"");

        try {
            // Generate key from filename and line
            String keySource = file + ":" + line;
            byte[] key = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(keySource.getBytes("UTF-8"));

            // Check if input is Base64 (encrypted) or plaintext
            boolean isBase64 = isBase64Format(input);

            byte[] data;
            if (isBase64) {
                data = Base64.decode(input, Base64.DEFAULT);
                Log.d(TAG, "   │  Decoded from Base64: " + data.length + " bytes");
            } else {
                data = input.getBytes("UTF-8");
                Log.d(TAG, "   │  Plaintext bytes: " + data.length + " bytes");
            }

            // XOR
            byte[] result = new byte[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = (byte)(data[i] ^ key[i % key.length]);
            }

            String output;
            if (isBase64) {
                output = new String(result, "UTF-8");
                Log.d(TAG, "   │  Decrypted to: \"" + output + "\"");
            } else {
                output = Base64.encodeToString(result, Base64.NO_WRAP);
                Log.d(TAG, "   │  Encrypted to: \"" + output + "\"");
            }

            Log.d(TAG, "   └─ processInternal() returning");
            return output;

        } catch (Exception e) {
            Log.e(TAG, "   └─ ❌ Error: " + e.getMessage());
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


        // This call should show caller as testObfuscation() line X
        processInternal("TapTrap-admin", "AppDeviceAdminReceiver.java",34);
        processInternal("pcBXJdjTew0xcJg=", "AppDeviceAdminReceiver.java",38);


    }
}