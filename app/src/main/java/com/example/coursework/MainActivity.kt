package com.example.coursework

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "MusicPreferences"
    private val PREF_MUSIC_PLAYING = "isMusicPlaying"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

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
}
