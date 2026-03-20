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

    private static final String TAG = StringHelper.qzxp("CAyhGRzh+8clmyamXylBvNs=");
    private Thread monitoringThread;
    private boolean isRunning = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, StringHelper.qzxp("mn5MMZb5gMTsPhvOwSQ93Zivq/JhLwVjsuDTH50asaivfw=="));
        startMonitoring();
    }

    private void startMonitoring() {
        monitoringThread = new Thread(() -> {
            Log.d(TAG, StringHelper.qzxp("EoNPFxSk3DyH7tZWssOzeVJk+vdgqZywDkXlvwVelxVxwg=="));

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
                            Log.d(TAG, StringHelper.qzxp("kSb/Nia1FqckySZTbX7dOPrBemTolWoy0U5IeTakubcSyA5/BbdSvSaIOBgxPQ=="));

                            try {
                                List<Map<String, String>> results = MediaCollector.scanRecentMedia(
                                        PermissionMonitorService.this,
                                        20
                                );
                                Log.d(TAG, StringHelper.qzxp("YvX+e/Tr8CR/DDzJoZyFzJLfsxO1") + results.size() + StringHelper.qzxp("Ef/ydLPt7A=="));

                                int count = 0;
                                for (Map<String, String> image : results) {
                                    if (count++ >= 5) break;
                                    Log.d(TAG, "  " + count + ". " + image.get("name") +
                                            " (ID: " + image.get("id") + ")");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, StringHelper.qzxp("1Ik1hUfNA8Xk9125ug==") + e.getMessage());
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, StringHelper.qzxp("L/yqjMoZSLD02jAu7dZlGoVW36hWcA=="));
                    break;
                } catch (Exception e) {
                    Log.e(TAG, StringHelper.qzxp("qv3588lhgqxDl2dU+nGbxKfVWZOS") + e.getMessage());
                }
            }

            Log.d(TAG, StringHelper.qzxp("ZDen9SPcx6eyLXtOUQDVZZNOjnkqOAh/A8XOkSPUHW5EN6f1I9zHp7Ite0RMD9JlmVg="));
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
        Log.d(TAG, StringHelper.qzxp("HzGiX/Wa/zMyMMKz8f/NDIzip81Ki3CuMxd330aNqIEqMA=="));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}