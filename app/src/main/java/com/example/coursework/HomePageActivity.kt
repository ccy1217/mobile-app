package com.example.coursework

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.ContextCompat

class HomePageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var mAuth: FirebaseAuth
    private val myTag = "joanne"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        mAuth = FirebaseAuth.getInstance()
        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
        Log.i(myTag, "Main Home Page loaded")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.nav_quiz -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QuizFragment()).commit()
            R.id.nav_history -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HistoryFragment()).commit()
            R.id.nav_setting -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingFragment()).commit()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)

        // Set the text color for the menu items
        val aboutUsItem = menu?.findItem(R.id.about_us_click)
        val logoutItem = menu?.findItem(R.id.logout_click)

        // Create a SpannableString for About Us item
        val aboutUsTitle = SpannableString(aboutUsItem?.title)
        val navyColor = ContextCompat.getColor(this, R.color.navy2) // Navy color
        aboutUsTitle.setSpan(ForegroundColorSpan(navyColor), 0, aboutUsTitle.length, 0)
        aboutUsItem?.title = aboutUsTitle

        // Create a SpannableString for Logout item
        val logoutTitle = SpannableString(logoutItem?.title)
        logoutTitle.setSpan(ForegroundColorSpan(navyColor), 0, logoutTitle.length, 0)
        logoutItem?.title = logoutTitle


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_click -> {
                logoutClick()
                return true
            }
            R.id.about_us_click -> {
                AboutUsDialogFragment().show(supportFragmentManager, "AboutUsDialog")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logoutClick() {
        Log.i(myTag, "Logout Clicked")
        mAuth.signOut()
        MusicPlayerManager.stopMusic() // Stop music on logout
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStop() {
        super.onStop()
        Log.i(myTag, "in onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicPlayerManager.releaseMusic()
    }
}
