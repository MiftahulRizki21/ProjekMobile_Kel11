package com.example.projekmobile_kel11

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FCM_TEST", "📩 Notifikasi diterima")

        val title = message.notification?.title ?: "Reminder"
        val body = message.notification?.body ?: "Ada reminder baru"

        showNotification(title, body)
    }

    override fun onNewToken(token: String) {
        Log.d("FCM_TEST", "🔥 Token baru: $token")

        // Simpan token ke Firestore (USER)
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(token) // atau userId
            .set(mapOf("fcmToken" to token))
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "reminder_channel"

        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "Reminder",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}

