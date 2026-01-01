package com.example.projekmobile_kel11.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.utils.NotificationUtil

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("REMINDER_TEST", "🚨 onReceive() TERPANGGIL")

        val title = intent.getStringExtra("title") ?: "Reminder"
        val message = intent.getStringExtra("message") ?: ""

        NotificationUtil.showNotification(context, title, message)
    }
}


