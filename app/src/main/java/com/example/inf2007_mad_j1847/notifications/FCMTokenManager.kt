package com.example.inf2007_mad_j1847.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging

object FCMTokenManager {

    private const val TAG = "FCMTokenManager"
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Retrieves the current FCM token and saves it to the user's Firestore document.
     * Call this after successful login/sign-up.
     */
    fun registerToken() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.w(TAG, "Cannot register token - no user logged in")
            return
        }

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.d(TAG, "FCM Token retrieved: ${token.take(20)}...")

                db.collection("users")
                    .document(uid)
                    .update("fcmToken", token)
                    .addOnSuccessListener {
                        Log.d(TAG, "FCM token saved for user: $uid")
                    }
                    .addOnFailureListener {
                        // If document doesn't exist yet, use merge
                        db.collection("users")
                            .document(uid)
                            .set(mapOf("fcmToken" to token), SetOptions.merge())
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get FCM token", e)
            }
    }

    /**
     * Clears the FCM token from Firestore.
     * Call this BEFORE signing out.
     */
    fun clearToken() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.w(TAG, "Cannot clear token - no user logged in")
            return
        }

        db.collection("users")
            .document(uid)
            .update("fcmToken", FieldValue.delete())
            .addOnSuccessListener {
                Log.d(TAG, "FCM token cleared for user: $uid")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to clear FCM token", e)
            }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}