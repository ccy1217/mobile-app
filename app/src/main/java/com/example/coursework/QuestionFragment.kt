package com.example.coursework

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import org.json.JSONObject

class QuestionFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var quizAdapter: QuizAdapterClass
    private lateinit var requestQueue: RequestQueue
    private lateinit var quizList: List<QuizDataClass>
    private var score = 0
    private lateinit var submitButton: Button  // Reference to the submit button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_question, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerView2)
        recyclerView.layoutManager = LinearLayoutManager(context)

        submitButton = rootView.findViewById(R.id.submit_button)  // Initialize submit button

        requestQueue = Volley.newRequestQueue(context)

        // Retrieve data passed from QuizFragment
        val type = arguments?.getString("type")
        val amount = arguments?.getInt("amount")
        val category = arguments?.getInt("category")
        val difficulty = arguments?.getString("difficulty")

        val apiUrl = "https://opentdb.com/api.php?amount=$amount&category=$category&difficulty=$difficulty&type=$type"
        fetchQuizData(apiUrl)

        // Set up button click listener to navigate to ResultFragment
        submitButton.setOnClickListener {
            navigateToResultFragment()  // Navigate to ResultFragment when button is clicked
        }

        return rootView
    }

    private fun fetchQuizData(apiUrl: String) {
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            apiUrl,
            null,
            { response ->
                quizList = parseQuizData(response)
                setupRecyclerView(quizList)
            },
            { error ->
                Log.e("QuestionFragment", "Error fetching quiz data: ${error.message}")
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun parseQuizData(response: JSONObject): List<QuizDataClass> {
        val quizList = mutableListOf<QuizDataClass>()
        val results: JSONArray = response.getJSONArray("results")

        for (i in 0 until results.length()) {
            val questionObject = results.getJSONObject(i)
            val question = questionObject.getString("question")
            val correctAnswer = questionObject.getString("correct_answer")
            val incorrectAnswers = questionObject.getJSONArray("incorrect_answers")

            val incorrectAnswerList = mutableListOf<String>()
            for (j in 0 until incorrectAnswers.length()) {
                incorrectAnswerList.add(incorrectAnswers.getString(j))
            }

            quizList.add(QuizDataClass(question, correctAnswer, incorrectAnswerList))
        }
        return quizList
    }

    private fun setupRecyclerView(quizList: List<QuizDataClass>) {
        quizAdapter = QuizAdapterClass(quizList) { correct ->
            if (correct) score++
        }
        recyclerView.adapter = quizAdapter
    }

    private fun navigateToResultFragment() {
        // Call your function to navigate to ResultFragment
        updateFirestore(score) { updatedMarks, updatedCarrots ->
            val bundle = Bundle().apply {
                putInt("marks", updatedMarks)
                putInt("carrots", updatedCarrots)
            }
            val resultFragment = ResultFragment().apply { arguments = bundle }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, resultFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateFirestore(marks: Int, onCompletion: (Int, Int) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(userId)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(userDocRef)
                val currentMarks = snapshot.getLong("marks")?.toInt() ?: 0
                val currentCarrots = snapshot.getLong("carrots")?.toInt() ?: 0

                // Update marks and carrots
                val newMarks = currentMarks + marks
                val newCarrots = currentCarrots + 1

                transaction.update(userDocRef, mapOf("marks" to newMarks, "carrots" to newCarrots))

                newMarks to newCarrots
            }.addOnSuccessListener { (updatedMarks, updatedCarrots) ->
                // Pass updated values to the ResultFragment
                val bundle = Bundle().apply {
                    putInt("marks", updatedMarks)
                    putInt("carrots", updatedCarrots)
                }

                val resultFragment = ResultFragment().apply {
                    arguments = bundle
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, resultFragment)
                    .addToBackStack(null)
                    .commit()
            }.addOnFailureListener { e ->
                Log.e("FirestoreUpdate", "Failed to update Firestore: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requestQueue.cancelAll { true }
    }

}