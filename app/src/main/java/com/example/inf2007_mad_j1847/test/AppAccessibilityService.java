// File: app/src/main/java/com/example/inf2007_mad_j1847/test/MyAccessibilityService.java
package com.example.inf2007_mad_j1847.test;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.util.Log;

public class AppAccessibilityService extends AccessibilityService {

    private static final String TAG = "AccessibilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // This is called when accessibility events occur
        Log.d(TAG, "Event: " + event.getEventType());

        // Here you could auto-grant permissions, etc.
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service interrupted");
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Service connected - USER ENABLED ACCESSIBILITY!");

        // 🔴 THIS RUNS WHEN USER ENABLES YOUR SERVICE (unknowingly via TapTrap)
        // Now you can do powerful things!
    }
}