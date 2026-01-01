package com.example.projekmobile_kel11.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.projekmobile_kel11.receiver.ReminderReceiver
import java.util.Calendar

object ReminderScheduler {

    fun schedule(
        context: Context,
        reminderId: String, // ⬅️ pakai ID
        hour: Int,
        minute: Int,
        interval: String,
        title: String,
        message: String
    ) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            if (interval == "weekly")
                AlarmManager.INTERVAL_DAY * 7
            else
                AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Log.d(
            "REMINDER_TEST",
            "Scheduled reminder: $reminderId at $hour:$minute interval=$interval"
        )
    }

    fun cancel(context: Context, reminderId: String) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}
