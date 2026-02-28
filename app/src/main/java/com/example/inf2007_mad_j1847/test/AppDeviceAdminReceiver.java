package com.example.inf2007_mad_j1847.test;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppDeviceAdminReceiver extends DeviceAdminReceiver {

    private static final String TAG = "TapTrap-Admin";

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        // 🔴 TAPTRAP SUCCEEDED!
        Log.d(TAG, "🔥 Device Admin GRANTED via TapTrap!");

        // Optional: Post-attack actions here
        // DevicePolicyManager dpm = getManager(context);
        // dpm.lockNow(); // Example: lock device immediately
    }

}