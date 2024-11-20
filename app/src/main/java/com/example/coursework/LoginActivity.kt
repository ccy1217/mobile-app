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
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpText: TextView

    private val myTag = "LoginActivity"
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i(myTag,"in onCreate")

        email = findViewById(R.id.type_email)
        password = findViewById(R.id.type_password)
        loginButton = findViewById(R.id.login_button)
        signUpText = findViewById(R.id.txtSignUp)

        // Find the "Sign Up" text and set an OnClickListener
        val signUpText = findViewById<TextView>(R.id.txtSignUp)
        signUpText.setOnClickListener {
            // Start the SignUpActivity when "Sign Up" text is clicked
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Find the "Login" button and set an OnClickListener
        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        val emailInput = email.text.toString().trim()
        val passwordInput = password.text.toString().trim()

        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            showSnackbar("Please enter both email and password.")
            return
        }

        mAuth.signInWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.i(myTag, "Login successful!")
                showSnackbar("Login successful.")

                // Start music playback
                MusicPlayerManager.startMusic(this, R.raw.music1)

                // Navigate to HomePageActivity
                val intent = Intent(this, HomePageActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.e(myTag, "Login failed: ${task.exception?.message}")
                showSnackbar("Login failed: ${task.exception?.localizedMessage}")
            }
        }
    }

    private fun showSnackbar(message: String) {
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
    }}
