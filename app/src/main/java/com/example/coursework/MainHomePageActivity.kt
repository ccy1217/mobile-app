package com.example.coursework

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class MainHomePageActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private val myTag = "joanne"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_homepage)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Set the Toolbar as the ActionBar
        val toolbar: Toolbar = findViewById(R.id.toolbar33)
        setSupportActionBar(toolbar)

        Log.i(myTag, "Main Home Page loaded")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> {
                // Navigate to Settings
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.logout_click -> {
                // Call logout function
                logoutClick()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logoutClick() {
        Log.i(myTag, "Logout Clicked")
        mAuth.signOut() // Firebase logout
        updateUIOnLogout()
    }

    private fun updateUIOnLogout() {
        // Redirect to login screen and finish this activity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStop() {
        super.onStop()
        Log.i(myTag, "in onStop")
        // Perform any necessary cleanup here (but do NOT log out the user)
    }
}
//ji