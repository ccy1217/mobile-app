package com.example.coursework

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity



class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Find the "Sign Up" text and set an OnClickListener
        val signUpText = findViewById<TextView>(R.id.txtSignUp)
        signUpText.setOnClickListener {
            // Start the SignUpActivity when "Sign Up" text is clicked
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Find the "Login" button and set an OnClickListener
        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener {
            // Start the MainHomePageActivity when "Login" button is clicked
            val intent = Intent(this, MainHomePageActivity::class.java)
            startActivity(intent)
            finish()

    }

}}
