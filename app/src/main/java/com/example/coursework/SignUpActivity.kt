package com.example.coursework

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    //play the music , need to use raw file, but where to put is a problem,
    //may be i need to create setting
    //do the notification when user create an account


    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var password2: EditText
    private lateinit var createAccount: Button

    private val myTag = "joanne"
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(myTag, "in onCreate")
        setContentView(R.layout.activity_sign_up)

        // Initialize UI elements
        name = findViewById(R.id.create_name)
        email = findViewById(R.id.create_email)
        password = findViewById(R.id.SignUpTypePassword)
        password2 = findViewById(R.id.SignUpConfirmPassword)
        createAccount = findViewById(R.id.signup_button)

        // Create account button click
        createAccount.setOnClickListener { v -> registerClick(v) }

        // Go to login page on "Sign In" text click
        val signInText = findViewById<TextView>(R.id.txtSignIn)
        signInText.setOnClickListener {
            Log.i(myTag, "Button click and go to login page")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerClick(view: View) {
        Log.i(myTag, "register Click")

        if (mAuth.currentUser != null) {
            displayMessage(view, getString(R.string.register_while_logged_in))
        } else {
            // Ensure passwords match
            if (password.text.toString() != password2.text.toString()) {
                displayMessage(view, "Passwords do not match")
                return
            }

            // Firebase create user with email and password
            mAuth.createUserWithEmailAndPassword(email.text.toString(), password2.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        closeKeyBoard()

                        // Display a congratulatory message
                        displayMessage(view, "Congratulations! You have successfully created an account. Now you can login.")

                        // Delay for a moment before navigating to Login Activity
                        view.postDelayed({
                            // Redirect to login page after success
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish() // Finish SignUpActivity
                        }, 2000) // 2-second delay
                    } else {
                        displayMessage(view, task.exception?.message.toString())
                    }
                }
        }
    }


    // Update the UI based on login status
    private fun updateUI() {
        Log.i(myTag, "in updateUI")
        val currentUser = mAuth.currentUser
        val greetingSpace = findViewById<TextView>(R.id.create_email)
        greetingSpace.text = if (currentUser != null) {
            getString(R.string.logged_in, currentUser.email)
        } else {
            getString(R.string.not_logged_in)
        }
    }

    // Show a message (Snackbar)
    private fun displayMessage(view: View, msgTxt: String) {
        Snackbar.make(view, msgTxt, Snackbar.LENGTH_SHORT).show()
    }

    // Hide keyboard
    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    // Lifecycle methods
    override fun onStart() {
        super.onStart()
        Log.i(myTag, "in onStart")
        updateUI()
    }

}