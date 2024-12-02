package com.example.coursework

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {

    private lateinit var showCarrot: TextView
    private lateinit var lastTimeFed: TextView
    private lateinit var countdownTimer: TextView
    private lateinit var feedButton: Button
    private lateinit var startQuizButton: Button
    private lateinit var rabbitImage: ImageView // Reference to the ImageView
    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val FEED_INTERVAL: Long = 1 * 60 * 1000 // 1 minute in milliseconds
    private var timer: CountDownTimer? = null
    private var isTimerRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        showCarrot = rootView.findViewById(R.id.show_carrot)
        lastTimeFed = rootView.findViewById(R.id.last_time_fed)
        countdownTimer = rootView.findViewById(R.id.countdown_timer)
        feedButton = rootView.findViewById(R.id.feed_button)
        startQuizButton = rootView.findViewById(R.id.startQuiz_button)
        rabbitImage = rootView.findViewById(R.id.rabbit_image) // Initialize the ImageView

        // Fetch user data
        fetchUserData(rootView)

        feedButton.setOnClickListener {
            onFeedButtonClicked(rootView)
        }

        // "Start Quiz" button navigates to the QuizFragment
        startQuizButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QuizFragment()) // Update with your container ID
                .addToBackStack(null)
                .commit()
        }

        return rootView
    }

    @SuppressLint("SetTextI18n")
    private fun fetchUserData(rootView: View) {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val userRef = db.collection("users").document(currentUser.uid)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Retrieve the carrot count from Firestore
                        val carrotCount = document.getLong("carrots") ?: 0
                        showCarrot.text = carrotCount.toString() // Update the UI with carrot count

                        // Retrieve the last feed time
                        val lastFeedTime = document.getLong("last_feed_time") ?: 0
                        startDynamicTimer(lastFeedTime, rootView)
                    } else {
                        Log.d("HomeFragment", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("HomeFragment", "Error getting documents: ", exception)
                    displayMessage(rootView, "Error fetching user data.")
                }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startDynamicTimer(lastFeedTime: Long, rootView: View) {
        val currentTime = System.currentTimeMillis()
        val timeElapsed = currentTime - lastFeedTime

        // Check if feeding interval has passed
        if (timeElapsed >= FEED_INTERVAL) {
            countdownTimer.text = "00:00:00" // Timer is ready
            isTimerRunning = false
            rabbitImage.setImageResource(R.drawable.cry) // Show cry image
        } else {
            val remainingTime = FEED_INTERVAL - timeElapsed

            // Start the countdown timer
            timer?.cancel() // Cancel any existing timer
            timer = object : CountDownTimer(remainingTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    isTimerRunning = true
                    countdownTimer.text = formatTime(millisUntilFinished)
                    rabbitImage.setImageResource(R.drawable.happy) // Show happy image during countdown
                }

                @SuppressLint("SetTextI18n")
                override fun onFinish() {
                    isTimerRunning = false
                    countdownTimer.text = "00:00:00"
                    rabbitImage.setImageResource(R.drawable.cry) // Revert to cry image
                }
            }.start()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(millis: Long): String {
        val seconds = millis / 1000 % 60
        val minutes = millis / (1000 * 60) % 60
        val hours = millis / (1000 * 60 * 60) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun onFeedButtonClicked(view: View) {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val userRef = db.collection("users").document(currentUser.uid)

            // Fetch the current carrot count and the timer value
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val carrotCount = document.getLong("carrots") ?: 0
                        val timerText = countdownTimer.text.toString()

                        // Check if the user has enough carrots
                        if (carrotCount < 3) {
                            displayMessage(view, "Not enough carrots to feed!")
                            return@addOnSuccessListener
                        }

                        // Check if the timer is not "00:00:00"
                        if (isTimerRunning) {
                            displayMessage(view, "The rabbit is full now, feed it later!")
                            return@addOnSuccessListener
                        }

                        // If the user has enough carrots and the timer is "00:00:00", proceed to feed
                        val updatedCarrotCount = carrotCount - 3
                        userRef.update(
                            mapOf(
                                "carrots" to updatedCarrotCount,
                                "last_feed_time" to System.currentTimeMillis()
                            )
                        ).addOnSuccessListener {
                            // Successfully updated the carrot count, update the UI again
                            fetchUserData(view)  // Refresh the carrot count UI
                            displayMessage(view, "Fed the rabbit! -3 Carrots")
                        }.addOnFailureListener { e ->
                            displayMessage(view, "Error feeding rabbit: ${e.message}")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    displayMessage(view, "Error fetching user data: ${e.message}")
                }
        }
    }

    private fun displayMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel() // Clean up the timer to avoid memory leaks
    }
}
