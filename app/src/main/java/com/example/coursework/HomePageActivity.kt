package com.example.coursework

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomePageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firestore: FirebaseFirestore
    private val myTag = "joanne"

    private val preferenceMusicPlaying = "pref_music_playing"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        sharedPreferences = getSharedPreferences("com.example.coursework", Context.MODE_PRIVATE)

        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val wasMusicPlaying = sharedPreferences.getBoolean(preferenceMusicPlaying, false)
        if (wasMusicPlaying) {
            MusicPlayerManager.startMusic(this, R.raw.music)
        }

        loadUserInfo(navigationView)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
        Log.i(myTag, "Main Home Page loaded")
    }

    private fun loadUserInfo(navigationView: NavigationView) {
        val headerView = navigationView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.txt1)
        val userEmailTextView = headerView.findViewById<TextView>(R.id.txt2)

        val currentUser = mAuth.currentUser
        currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: "User Name"
                        val email = document.getString("email") ?: "user@example.com"
                        userNameTextView.text = name
                        userEmailTextView.text = email
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(myTag, "Error loading user info: ${exception.message}")
                }
        }
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
        val aboutUsItem = menu?.findItem(R.id.about_us_click)
        val logoutItem = menu?.findItem(R.id.logout_click)

        val navyColor = ContextCompat.getColor(this, R.color.navy)

        aboutUsItem?.let {
            val aboutUsTitle = SpannableString(it.title)
            aboutUsTitle.setSpan(ForegroundColorSpan(navyColor), 0, aboutUsTitle.length, 0)
            it.title = aboutUsTitle
        }

        logoutItem?.let {
            val logoutTitle = SpannableString(it.title)
            logoutTitle.setSpan(ForegroundColorSpan(navyColor), 0, logoutTitle.length, 0)
            it.title = logoutTitle
        }
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
        MusicPlayerManager.stopMusic()
        sharedPreferences.edit().apply {
            putBoolean(preferenceMusicPlaying, false)
            apply()
        }
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicPlayerManager.releaseMusic()
    }
}
