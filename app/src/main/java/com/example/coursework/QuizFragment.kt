package com.example.coursework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment

class QuizFragment : Fragment() {

    private lateinit var typeSpinner: Spinner
    private lateinit var numberSpinner: Spinner
    private lateinit var categorySpinner: Spinner
    private lateinit var difficultySpinner: Spinner
    private lateinit var nextButton: Button

    private val categoryMap = mapOf(
        "General Knowledge" to 9,
        "Entertainment: Books" to 10,
        "Entertainment: Film" to 11,
        "Entertainment: Music" to 12,
        "Entertainment: Musicals & Theatres" to 13,
        "Entertainment: Televisions" to 14,
        "Entertainment: Video games" to 15,
        "Entertainment: Board Games" to 16,
        "Science & Nature" to 17,
        "Science: Computers" to 18,
        "Science: Mathematics" to 19,
        "Mythology" to 20,
        "Sports" to 21,
        "Geography" to 22,
        "History" to 23,
        "Politics" to 24,
        "Art" to 25,
        "Celebrities" to 26,
        "Animals" to 27,
        "Vehicles" to 28,
        "Entertainment: Comics" to 29,
        "Science: Gadgets" to 30,
        "Entertainment: Japanese Anime & Manga" to 31,
        "Entertainment: Cartoon & Animations" to 32,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_quiz, container, false)

        // Initialize spinners
        typeSpinner = rootView.findViewById(R.id.spinner1)
        numberSpinner = rootView.findViewById(R.id.spinner2)
        categorySpinner = rootView.findViewById(R.id.spinner3)
        difficultySpinner = rootView.findViewById(R.id.spinner4)
        nextButton = rootView.findViewById(R.id.next_button)

        // Set button click listener
        nextButton.setOnClickListener {
            val selectedType = typeSpinner.selectedItem.toString()
            val selectedNumber = numberSpinner.selectedItem.toString().toInt()
            val selectedCategory = categorySpinner.selectedItem.toString()
            val selectedDifficulty = difficultySpinner.selectedItem.toString()

            val typeParam = if (selectedType == "Multiple Choice") "multiple" else "boolean"
            val categoryParam = categoryMap[selectedCategory] ?: 9
            val difficultyParam = selectedDifficulty.lowercase()

            // Create a Bundle to pass data
            val bundle = Bundle().apply {
                putString("type", typeParam)
                putInt("amount", selectedNumber)
                putInt("category", categoryParam)
                putString("difficulty", difficultyParam)
            }

            // Create the QuestionFragment and pass the data
            val questionFragment = QuestionFragment()
            questionFragment.arguments = bundle

            // Replace QuizFragment with QuestionFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, questionFragment)
                .addToBackStack(null)  // Allows us to go back to the previous fragment
                .commit()
        }

        return rootView
    }
}