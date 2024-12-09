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
    private lateinit var rabbitImage: ImageView
    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val feedInterval: Long = 10 * 1000 // 1 minute in milliseconds
    private var timer: CountDownTimer? = null
    private var isTimerRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize UI components
        showCarrot = rootView.findViewById(R.id.show_carrot)
        lastTimeFed = rootView.findViewById(R.id.last_time_fed)
        countdownTimer = rootView.findViewById(R.id.countdown_timer)
        feedButton = rootView.findViewById(R.id.feed_button)
        startQuizButton = rootView.findViewById(R.id.startQuiz_button)
        rabbitImage = rootView.findViewById(R.id.rabbit_image)

        // Fetch user data
        fetchUserData(rootView)

        feedButton.setOnClickListener {
            onFeedButtonClicked(rootView)
        }

        startQuizButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QuizFragment())
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
                        // Retrieve carrot count
                        val carrotCount = document.getLong("carrots") ?: 0
                        showCarrot.text = carrotCount.toString()

                        // Retrieve last feed time (if it exists)
                        val lastFeedTime = document.getLong("last_feed_time")

                        if (lastFeedTime != null && lastFeedTime > 0) {
                            lastTimeFed.text = formatTimestamp(lastFeedTime)
                            startDynamicTimer(lastFeedTime, rootView)
                        } else {
                            // First-time login (last_feed_time is null)
                            lastTimeFed.text = "--" // Display "--" if the user has never fed
                            countdownTimer.text = "00:00:00"
                            rabbitImage.setImageResource(R.drawable.cry)
                            isTimerRunning = false // Don't start the timer automatically
                        }
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

        if (timeElapsed >= feedInterval) {
            countdownTimer.text = "00:00:00"
            isTimerRunning = false
            rabbitImage.setImageResource(R.drawable.cry)
        } else {
            val remainingTime = feedInterval - timeElapsed

            timer?.cancel()
            timer = object : CountDownTimer(remainingTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    isTimerRunning = true
                    countdownTimer.text = formatTime(millisUntilFinished)
                    rabbitImage.setImageResource(R.drawable.happy)
                }

                override fun onFinish() {
                    isTimerRunning = false
                    countdownTimer.text = "00:00:00"
                    rabbitImage.setImageResource(R.drawable.cry)
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

    @SuppressLint("SimpleDateFormat")
    private fun formatTimestamp(timestamp: Long): String {
        val date = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp)
        return date
    }

    private fun onFeedButtonClicked(view: View) {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val userRef = db.collection("users").document(currentUser.uid)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val carrotCount = document.getLong("carrots") ?: 0

                        // Prevent feeding if the user has less than 3 carrots
                        if (carrotCount < 3) {
                            displayMessage(view, "Not enough carrots to feed!")
                            return@addOnSuccessListener
                        }

                        if (isTimerRunning) {
                            displayMessage(view, "The rabbit is full now, feed it later!")
                            return@addOnSuccessListener
                        }

                        val updatedCarrotCount = carrotCount - 3
                        val currentTime = System.currentTimeMillis()

                        // Update the last_feed_time in Firestore when the user feeds the rabbit
                        userRef.update(
                            mapOf(
                                "carrots" to updatedCarrotCount,
                                "last_feed_time" to currentTime
                            )
                        ).addOnSuccessListener {
                            fetchUserData(view)  // Fetch updated data after feeding
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
        timer?.cancel() // Cancel the timer to prevent memory leaks or unexpected behavior
    }
}
