package com.example.coursework

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Create Notification Channel for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        // Check if the user is already logged in
        if (mAuth.currentUser != null) {
            // Check Firestore for music preference
            val userId = mAuth.currentUser?.uid
            if (userId != null) {
                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        val isMusicPlaying = document.getBoolean("music_playing") ?: false
                        if (isMusicPlaying) {
                            MusicPlayerManager.startMusic(this, R.raw.music)
                        }
                    }
                    .addOnFailureListener {
                        // Handle error (optional)
                    }
            }

            // Navigate to MainPageActivity
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Navigate to LoginActivity if not logged in
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun createNotificationChannel() {
        val channelId = "YOUR_CHANNEL_ID"
        val channelName = "Default Channel"
        val channelDescription = "This channel is used for app notifications"

        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }
}
