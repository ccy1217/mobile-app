package com.example.coursework

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChooseActivity : AppCompatActivity() {
    private val myTag= "joanne"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

        // Set up buttons for Home and Logout
        val homeIcon = findViewById<ImageView>(R.id.left_icon)
        val logoutIcon = findViewById<ImageView>(R.id.right_icon)

        // Navigate to HomePage1Activity when homeIcon is clicked
        homeIcon.setOnClickListener {
            val intent = Intent(this, MainHomePageActivity::class.java)
            startActivity(intent)
        }

        // Navigate to LoginPageActivity when logoutIcon is clicked
        logoutIcon.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java) // Replace with your actual login activity class
            startActivity(intent)
            finish()  // Close HomePageActivity to prevent going back after logging out
        }
    }
}
//hi