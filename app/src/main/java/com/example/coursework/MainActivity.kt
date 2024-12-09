package com.example.coursework

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private val preferenceName = "MusicPreferences"
    private val PREF_MUSIC_PLAYING = "isMusicPlaying"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

        // Create Notification Channel for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        // Check if the user is already logged in
        if (mAuth.currentUser != null) {
            // Check if music was previously playing
            val isMusicPlaying = sharedPreferences.getBoolean(PREF_MUSIC_PLAYING, false)

            // If music was playing, start it
            if (isMusicPlaying) {
                MusicPlayerManager.startMusic(this, R.raw.music)
            }

            // Navigate to HomePageActivity
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Navigate to LoginActivity if not logged in
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Create Notification Channel
    private fun createNotificationChannel() {
        val channelId = "YOUR_CHANNEL_ID"
        val channelName = "Default Channel"
        val channelDescription = "This channel is used for app notifications"

        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        // Register the channel with the system
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }
}
