package com.example.coursework

import android.text.Html
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuestionFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var quizAdapter: CustomiseQuizAdapterClass
    private lateinit var requestQueue: RequestQueue
    private lateinit var quizList: List<CustomiseQuizDataClass>
    private var score = 0
    private lateinit var submitButton: Button

    private lateinit var type: String
    private lateinit var difficulty: String
    private var category: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_question, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerView2)
        recyclerView.layoutManager = LinearLayoutManager(context)

        submitButton = rootView.findViewById(R.id.submit_button)
        requestQueue = Volley.newRequestQueue(context)

        // Retrieve data passed from QuizFragment
        type = arguments?.getString("type").orEmpty()
        val amount = arguments?.getInt("amount") ?: 10
        category = arguments?.getInt("category") ?: 0 // Default to 0 for "Any Category"
        difficulty = arguments?.getString("difficulty").orEmpty()

        // Construct the API URL based on the selected options
        val apiUrl = if (category == 0) {
            // Exclude the category parameter for "Any Category"
            "https://opentdb.com/api.php?amount=$amount&difficulty=$difficulty&type=$type"
        } else {
            // Include the category parameter for specific categories
            "https://opentdb.com/api.php?amount=$amount&category=$category&difficulty=$difficulty&type=$type"
        }

        fetchQuizData(apiUrl)

        // Set up button click listener to navigate to ResultFragment
        submitButton.setOnClickListener {
            Log.d("QuestionFragment", "Submit button clicked")
            navigateToResultFragment()
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

    private fun parseQuizData(response: JSONObject): List<CustomiseQuizDataClass> {
        val quizList = mutableListOf<CustomiseQuizDataClass>()
        val results: JSONArray = response.getJSONArray("results")

        for (i in 0 until results.length()) {
            val questionObject = results.getJSONObject(i)

            // Decode HTML entities for the question and answers
            val question = Html.fromHtml(questionObject.getString("question"), Html.FROM_HTML_MODE_LEGACY).toString()
            val correctAnswer = Html.fromHtml(questionObject.getString("correct_answer"), Html.FROM_HTML_MODE_LEGACY).toString()
            val incorrectAnswers = questionObject.getJSONArray("incorrect_answers")

            val incorrectAnswerList = mutableListOf<String>()
            for (j in 0 until incorrectAnswers.length()) {
                val decodedAnswer = Html.fromHtml(incorrectAnswers.getString(j), Html.FROM_HTML_MODE_LEGACY).toString()
                incorrectAnswerList.add(decodedAnswer)
            }

            quizList.add(CustomiseQuizDataClass(question, correctAnswer, incorrectAnswerList))
        }
        return quizList
    }


    private fun setupRecyclerView(quizList: List<CustomiseQuizDataClass>) {
        quizAdapter = CustomiseQuizAdapterClass(quizList) { correct ->
            if (correct) score++
        }
        recyclerView.adapter = quizAdapter
    }

    private fun navigateToResultFragment() {
        updateFirestore(score) { updatedMarks, updatedCarrots ->
            val bundle = Bundle().apply {
                putInt("marks", updatedMarks) // Total accumulated marks
                putInt("carrots", updatedCarrots) // Total accumulated carrots
                putInt("correctAnswers", score) // Current round's correct answers
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

            val currentDateTime = SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.getDefault()).format(Date())
            val quizAttemptId = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())

            db.runTransaction { transaction ->
                val snapshot = transaction.get(userDocRef)
                val currentMarks = snapshot.getLong("marks")?.toInt() ?: 0
                val currentCarrots = snapshot.getLong("carrots")?.toInt() ?: 0

                val newMarks = currentMarks + marks
                val newCarrots = currentCarrots + 1
                transaction.update(userDocRef, mapOf("marks" to newMarks, "carrots" to newCarrots))

                newMarks to newCarrots
            }.addOnSuccessListener { (updatedMarks, updatedCarrots) ->
                val totalQuestions = quizList.size
                val correctAnswers = score
                val wrongAnswers = totalQuestions - correctAnswers
                val categoryName = arguments?.getString("categoryName") ?: "Unknown"
                //val categoryId = arguments?.getInt("category") ?: 9
                //val categoryName = arguments?.getString("category")?: "Unknown"
                Log.d("FirestoreUpdate", "Saving quiz result with categoryName: $categoryName")

                val quizResultData = hashMapOf(
                    "marks" to marks,
                    "dateTime" to currentDateTime,
                    "quizType" to if (type == "boolean") "True/False" else "Multiple Choice",
                    "difficulty" to difficulty,
                    "category" to categoryName,
                    "totalQuestions" to totalQuestions,
                    "correctAnswers" to correctAnswers,
                    "wrongAnswers" to wrongAnswers,
                    "attemptId" to quizAttemptId
                )

                val quizResultsRef = userDocRef.collection("quizResults")

                quizResultsRef.whereEqualTo("attemptId", quizAttemptId).get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            quizResultsRef.add(quizResultData)
                                .addOnSuccessListener {
                                    onCompletion(updatedMarks, updatedCarrots)
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FirestoreUpdate", "Failed to add quiz result: ${e.message}")
                                }
                        } else {
                            onCompletion(updatedMarks, updatedCarrots)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreQuery", "Error checking duplicate results: ${e.message}")
                    }
            }.addOnFailureListener { e ->
                Log.e("FirestoreUpdate", "Failed to update Firestore: ${e.message}")
            }
        }
    }


}
