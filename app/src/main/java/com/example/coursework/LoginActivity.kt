package com.example.coursework

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpText: TextView
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Check if the user is already logged in
        if (mAuth.currentUser != null) {
            // If the user is already logged in, send them directly to HomePageActivity
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
            finish() // Close the LoginActivity
            return
        }

        email = findViewById(R.id.type_email)
        password = findViewById(R.id.type_password)
        loginButton = findViewById(R.id.login_button)
        signUpText = findViewById(R.id.txtSignUp)

        // SignUpActivity navigation
        signUpText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Login button functionality
        loginButton.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        val emailInput = email.text.toString().trim()
        val passwordInput = password.text.toString().trim()

        // Check if fields are empty
        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            showSnackbar("Please enter both email and password.")
            return
        }

        // Attempt to login
        mAuth.signInWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Login successful
                showSnackbar("Login successful.")

                // Start music playback (if needed)
                MusicPlayerManager.startMusic(this, R.raw.music)

                // Navigate to HomePageActivity
                val intent = Intent(this, MainPageActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // If login fails, show a general error message
                showSnackbar("Login failed. Please check your credentials.")
            }
        }
    }

    private fun showSnackbar(message: String) {
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
    }
}
