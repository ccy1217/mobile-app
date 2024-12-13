package com.example.coursework

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class SettingFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private val preferenceMusicVolume = "musicVolume"
    private val preferenceNotification = "notification_enabled"

    private lateinit var timePicker: TimePicker
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentDateTextView: TextView
    private lateinit var currentTimeTextView: TextView
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var musicSwitch: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var notificationSwitch: Switch

    private val handler = Handler()
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updateCurrentTime()
            handler.postDelayed(this, 1000) // Update time every second
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = requireContext().getSharedPreferences("com.example.coursework", Context.MODE_PRIVATE)

        // Initialize views
        musicSwitch = view.findViewById(R.id.switch1)
        val seekBar = view.findViewById<SeekBar>(R.id.seekbar1)
        timePicker = view.findViewById(R.id.timePicker)
        currentDateTextView = view.findViewById(R.id.current_date)
        currentTimeTextView = view.findViewById(R.id.current_time)
        notificationSwitch = view.findViewById(R.id.notificationSwitch)

        // Load the saved volume preference
        val savedVolume = sharedPreferences.getFloat(preferenceMusicVolume, 0.4f)
        seekBar.progress = (savedVolume * 100).toInt()

        // Retrieve the music preference from Firestore and set the initial state
        loadMusicPreference { isMusicPlaying ->
            musicSwitch.isChecked = isMusicPlaying
            if (isMusicPlaying) {
                MusicPlayerManager.startMusic(requireContext(), R.raw.music)
            }
        }

        // Set up the music switch
        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                MusicPlayerManager.startMusic(requireContext(), R.raw.music)
                showToast("Music started")
            } else {
                MusicPlayerManager.stopMusic()
                showToast("Music stopped")
            }
            // Save the music preference to Firestore
            saveMusicPreference(isChecked)
        }

        // Set up the volume control
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                MusicPlayerManager.setVolume(volume)
                // Save the volume level in SharedPreferences
                sharedPreferences.edit().apply {
                    putFloat(preferenceMusicVolume, volume)
                    apply()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Update current date and time every second
        handler.post(updateTimeRunnable)

        // Handle notification switch toggle
        val isNotificationEnabled = sharedPreferences.getBoolean(preferenceNotification, false)
        notificationSwitch.isChecked = isNotificationEnabled

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Store the notification state in SharedPreferences
            sharedPreferences.edit().apply {
                putBoolean(preferenceNotification, isChecked)
                apply()
            }

            if (isChecked) {
                startCheckingNotificationTime() // Start checking time for notifications
            } else {
                stopCheckingNotificationTime() // Stop checking time for notifications
            }
        }

        // Set the notification time
        view.findViewById<View>(R.id.setNotificationButton)?.setOnClickListener {
            setNotificationTime()
        }

        // Load the current notification time from Firestore
        loadNotificationTime()
    }

    private fun loadMusicPreference(callback: (Boolean) -> Unit) {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val isMusicPlaying = document.getBoolean("music_playing") ?: false
                        callback(isMusicPlaying)
                    } else {
                        callback(false)
                    }
                }
                .addOnFailureListener {
                    callback(false)
                }
        } else {
            callback(false)
        }
    }

    private fun saveMusicPreference(isMusicPlaying: Boolean) {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val userRef = db.collection("users").document(userId)
            userRef.update("music_playing", isMusicPlaying)
                .addOnSuccessListener {
                    // Successfully saved preference
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error saving music preference: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setNotificationTime() {
        val hour = timePicker.hour
        val minute = timePicker.minute
        val formattedTime = String.format("%02d:%02d", hour, minute)

        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val userRef = db.collection("users").document(userId)
            userRef.update("notification_time", formattedTime)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Notification time set to $formattedTime", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error setting time: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadNotificationTime() {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val notificationTime = document.getString("notification_time")
                        if (!notificationTime.isNullOrEmpty()) {
                            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)
                            if (notificationTime == currentTime) {
                                sendNotification()
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error loading time: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun startCheckingNotificationTime() {
        handler.post(updateTimeRunnable)
    }

    private fun stopCheckingNotificationTime() {
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun sendNotification() {
        NotificationReceiver().onReceive(requireContext(), Intent())
    }

    private fun updateCurrentTime() {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)

        currentDateTextView.text = currentDate
        currentTimeTextView.text = currentTime

        if (sharedPreferences.getBoolean(preferenceNotification, false)) {
            checkTimeAndNotify(currentTime)
        }
    }

    private fun checkTimeAndNotify(currentTime: String) {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val notificationTime = document.getString("notification_time")
                        if (notificationTime == currentTime) {
                            sendNotification()
                        }
                    }
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable) // Stop the time updates when fragment is destroyed
    }
}
