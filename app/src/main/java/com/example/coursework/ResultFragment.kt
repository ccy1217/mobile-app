package com.example.coursework

import android.annotation.SuppressLint
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
    private lateinit var quizCommentTextView: TextView
    private lateinit var takeButton: Button
    private lateinit var retakeButton: Button

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_result, container, false)

        scoreTextView = rootView.findViewById(R.id.count_marks)
        carrotTextView = rootView.findViewById(R.id.count_carrot)
        quizCommentTextView = rootView.findViewById(R.id.quizComment)
        takeButton = rootView.findViewById(R.id.take_button)
        retakeButton = rootView.findViewById(R.id.retake_button)

        val marks = arguments?.getInt("marks") ?: 0
        val carrots = arguments?.getInt("carrots") ?: 0
        val correctAnswers = arguments?.getInt("correctAnswers") ?: 0

        scoreTextView.text = "$marks"
        carrotTextView.text = "$carrots"
        quizCommentTextView.text = "You scored $correctAnswers correct answers in this round and gained 1 carrot!"

        takeButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QuizFragment())
                .addToBackStack(null)
                .commit()
        }

        retakeButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QuizFragment())
                .addToBackStack(null)
                .commit()
        }

        return rootView
    }
}
