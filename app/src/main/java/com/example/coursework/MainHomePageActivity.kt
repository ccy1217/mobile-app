package com.example.coursework

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainHomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_homepage)

        val quizIcon = findViewById<Button>(R.id.start_quiz_button)
        val historyIcon = findViewById<Button>(R.id.history_button)

        quizIcon.setOnClickListener {
            val intent = Intent(this, ChooseActivity::class.java) // Replace with your actual homepage activity class
            startActivity(intent)
        }

        // Navigate to LoginPageActivity when logoutIcon is clicked
        historyIcon.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java) // Replace with your actual login activity class
            startActivity(intent)
            finish()  // Close HomePageActivity to prevent going back after logging out
        }


    }}