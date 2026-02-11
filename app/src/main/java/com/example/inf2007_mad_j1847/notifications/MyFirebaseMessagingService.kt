package com.example.inf2007_mad_j1847.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.inf2007_mad_j1847.MainActivity
import com.example.inf2007_mad_j1847.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        const val MESSAGES_CHANNEL_ID = "messages_channel"
        const val APPOINTMENTS_CHANNEL_ID = "appointments_channel"

        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val messagesChannel = NotificationChannel(
                    MESSAGES_CHANNEL_ID, "Messages",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "New message notifications"
                    enableVibration(true)
                }

                val appointmentsChannel = NotificationChannel(
                    APPOINTMENTS_CHANNEL_ID, "Appointments",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Appointment notifications"
                    enableVibration(true)
                }

                manager.createNotificationChannel(messagesChannel)
                manager.createNotificationChannel(appointmentsChannel)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        saveTokenToFirestore(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val type = data["type"] ?: ""

        when (type) {
            "NEW_MESSAGE" -> handleNewMessageNotification(remoteMessage)
            "NEW_APPOINTMENT" -> handleAppointmentNotification(remoteMessage)
            "APPOINTMENT_CANCELLED" -> handleCancellationNotification(remoteMessage)
            else -> {
                remoteMessage.notification?.let {
                    showNotification(
                        it.title ?: "Notification",
                        it.body ?: "",
                        MESSAGES_CHANNEL_ID
                    )
                }
            }
        }
    }

    private fun handleNewMessageNotification(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val senderId = data["senderId"] ?: ""
        val senderName = data["senderName"] ?: "Someone"

        val title = remoteMessage.notification?.title ?: senderName
        val body = remoteMessage.notification?.body ?: "New message"

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateTo", "messaging_screen")
            putExtra("chatId", senderId)
            putExtra("chatName", senderName)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, senderId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, MESSAGES_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(senderId.hashCode(), notification)
    }

    private fun handleAppointmentNotification(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val appointmentId = data["appointmentId"] ?: ""

        val title = remoteMessage.notification?.title ?: "New Appointment"
        val body = remoteMessage.notification?.body ?: "You have a new appointment"

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateTo", "appointment_details")
            putExtra("appointmentId", appointmentId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, appointmentId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, APPOINTMENTS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(appointmentId.hashCode(), notification)
    }

    private fun handleCancellationNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "Appointment Cancelled"
        val body = remoteMessage.notification?.body ?: "An appointment has been cancelled"
        showNotification(title, body, APPOINTMENTS_CHANNEL_ID)
    }

    private fun showNotification(title: String, body: String, channelId: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun saveTokenToFirestore(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .update("fcmToken", token)
            .addOnSuccessListener { Log.d(TAG, "Token saved for $uid") }
            .addOnFailureListener { Log.e(TAG, "Failed to save token", it) }
    }
}