package com.example.coursework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ResultFragment : Fragment() {

    private lateinit var scoreTextView: TextView
    private lateinit var carrotTextView: TextView
    private lateinit var takeButton: Button
    private lateinit var retakeButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_result, container, false)

        // Initialize views
        scoreTextView = rootView.findViewById(R.id.count_marks)
        carrotTextView = rootView.findViewById(R.id.count_carrot)
        takeButton = rootView.findViewById(R.id.take_button)
        retakeButton = rootView.findViewById(R.id.retake_button)

        // Retrieve data passed from QuestionFragment
        val marks = arguments?.getInt("marks") ?: 0
        val carrots = arguments?.getInt("carrots") ?: 0

        scoreTextView.text = "$marks"
        carrotTextView.text = "$carrots"

        // Set button click listeners
        takeButton.setOnClickListener {
            // Navigate back to QuizFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QuizFragment())
                .addToBackStack(null)
                .commit()
        }

        retakeButton.setOnClickListener {
            // Retake the quiz (go back to the QuizFragment to choose quiz parameters)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QuizFragment())
                .addToBackStack(null)
                .commit()
        }

        return rootView
    }
}