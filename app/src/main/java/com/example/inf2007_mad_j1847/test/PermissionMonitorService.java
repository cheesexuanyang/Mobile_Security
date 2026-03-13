package com.example.inf2007_mad_j1847.test;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.content.ContextCompat;
import java.util.List;
import java.util.Map;

public class PermissionMonitorService extends Service {

    private static final String TAG = "PermissionMonitor";
    private Thread monitoringThread;
    private boolean isRunning = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Permission monitor service started");
        startMonitoring();
    }

    private void startMonitoring() {
        monitoringThread = new Thread(() -> {
            Log.d(TAG, "Monitoring for permission grant...");

            boolean permissionGranted = false;

            while (isRunning && !permissionGranted) {
                try {
                    Thread.sleep(5000); // Check every 5 seconds

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        String permission = android.Manifest.permission.READ_MEDIA_IMAGES;
                        int result = ContextCompat.checkSelfPermission(
                                PermissionMonitorService.this,
                                permission
                        );

                        if (result == PackageManager.PERMISSION_GRANTED) {
                            permissionGranted = true;
                            Log.d(TAG, "✅ Media permission granted! Starting scan...");

                            // Run scan
                            try {
                                List<Map<String, String>> results = MediaCollector.scanRecentMedia(
                                        PermissionMonitorService.this,
                                        20
                                );
                                Log.d(TAG, "Scan complete! Found " + results.size() + " images");

                                // Log first few images found
                                int count = 0;
                                for (Map<String, String> image : results) {
                                    if (count++ >= 5) break;
                                    Log.d(TAG, "  " + count + ". " + image.get("name") +
                                            " (ID: " + image.get("id") + ")");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Scan failed: " + e.getMessage());
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, "Monitoring interrupted");
                    break;
                } catch (Exception e) {
                    Log.e(TAG, "Error in monitoring: " + e.getMessage());
                }
            }

            Log.d(TAG, "Permission monitoring stopped - permission granted");
        });

        monitoringThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Restart if killed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        if (monitoringThread != null) {
            monitoringThread.interrupt();
        }
        Log.d(TAG, "Permission monitor service stopped");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}