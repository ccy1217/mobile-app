package com.example.coursework

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.coursework.R

class NotificationReceiver : BroadcastReceiver() {

    @SuppressLint("ObsoleteSdkInt")
    override fun onReceive(context: Context, intent: Intent?) {
        // Check if the POST_NOTIFICATIONS permission is granted
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, skip showing the notification
            return
        }

        // Create notification channel (for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "YOUR_CHANNEL_ID"
            val channelName = "Channel Name"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, "YOUR_CHANNEL_ID")
            .setContentTitle("Scheduled Notification")
            .setContentText("This is your scheduled notification!")
            .setSmallIcon(R.drawable.carrot) // Replace with your app's icon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(0, notification) // Use unique ID if sending multiple notifications
    }
}

