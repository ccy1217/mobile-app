package com.example.coursework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class QuizFragment : Fragment() {

    private lateinit var typeSpinner: Spinner
    private lateinit var numberSpinner: Spinner
    private lateinit var categorySpinner: Spinner
    private lateinit var difficultySpinner: Spinner
    private lateinit var nextButton: Button

    private val categoryMap = mapOf(
        "Animals" to 27,
        "Art" to 25,
        "Geography" to 22
        // Add more categories as needed
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

            val apiUrl = "https://opentdb.com/api.php?amount=$selectedNumber&category=$categoryParam&difficulty=$difficultyParam&type=$typeParam"

            preCheckAvailability(apiUrl) { available ->
                if (available) {
                    val bundle = Bundle().apply {
                        putString("type", typeParam)
                        putInt("amount", selectedNumber)
                        putInt("category", categoryParam)
                        putString("difficulty", difficultyParam)
                    }

                    val questionFragment = QuestionFragment()
                    questionFragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, questionFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(context, "No questions available for the selected options. Please try different options.", Toast.LENGTH_LONG).show()
                }
            }
        }

        return rootView
    }

    private fun preCheckAvailability(apiUrl: String, callback: (Boolean) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            apiUrl,
            null,
            { response ->
                val results = response.optJSONArray("results")
                callback(results != null && results.length() > 0)
            },
            { error ->
                Toast.makeText(context, "Error checking availability: ${error.message}", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        )
        requestQueue.add(jsonObjectRequest)
    }
}