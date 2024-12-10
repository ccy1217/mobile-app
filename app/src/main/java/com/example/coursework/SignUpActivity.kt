package com.example.coursework

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var password2: EditText
    private lateinit var createAccount: Button
    private lateinit var db: FirebaseFirestore

    private val myTag = "joanne"
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // UI elements
        name = findViewById(R.id.create_name)
        email = findViewById(R.id.create_email)
        password = findViewById(R.id.SignUpTypePassword)
        password2 = findViewById(R.id.SignUpConfirmPassword)
        createAccount = findViewById(R.id.signup_button)

        // Check if the user is already logged in
        if (mAuth.currentUser != null) {
            navigateToHomePage()
            return
        }

        // Create account button click
        createAccount.setOnClickListener { v -> registerClick(v) }

        // Redirect to login page on "Sign In" text click
        val signInText = findViewById<TextView>(R.id.txtSignIn)
        signInText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerClick(view: View) {
        Log.i(myTag, "Register button clicked")

        // Check if passwords match
        if (password.text.toString() != password2.text.toString()) {
            displayMessage(view, "Passwords do not match")
            return
        }

        // Create a user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Hide keyboard
                    closeKeyBoard()

                    // Store user data in Firestore
                    storeUserData(view)

                    // Display a success message
                    displayMessage(
                        view,
                        "Account created successfully! Redirecting to login page..."
                    )

                    // Redirect to Login Page after 2 seconds
                    view.postDelayed({
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }, 2000)
                } else {
                    // Handle failure
                    displayMessage(view, "Sign Up failed: ${task.exception?.message}")
                }
            }
    }

    private fun storeUserData(view: View) {
        val currentUser = mAuth.currentUser

        val userData = hashMapOf(
            "name" to name.text.toString(),
            "email" to currentUser?.email, // Firebase already handles the password securely
            "marks" to 0 ,
            "carrots" to 0,
        )

        db.collection("users")
            .document(currentUser?.uid ?: "")
            .set(userData)
            .addOnSuccessListener {
                Log.d(myTag, "User data successfully stored in Firestore")
            }
            .addOnFailureListener { e ->
                Log.w(myTag, "Error storing user data in Firestore", e)
                displayMessage(view, "Error saving user details. Please try again.")
            }
    }

    private fun displayMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun navigateToHomePage() {
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
        finish()
    }
}

