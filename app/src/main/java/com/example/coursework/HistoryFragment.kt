package com.example.coursework

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapterClass
    private lateinit var historyList: ArrayList<HistoryDataClass>
    private lateinit var searchList: ArrayList<HistoryDataClass>
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        searchView = view.findViewById(R.id.search)
        historyList = arrayListOf()
        searchList = arrayListOf()
        historyAdapter = HistoryAdapterClass(searchList)
        recyclerView.adapter = historyAdapter

        fetchHistoryData()

        setupSearchView()

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchHistoryData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(userId)
            val quizResultsRef = userDocRef.collection("quizResults")

            quizResultsRef.get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val category = document.getString("category") ?: "Unknown"
                        val dateTime = document.getString("dateTime") ?: "N/A"
                        val score = document.getLong("marks")?.toInt() ?: 0
                        val totalQuestions = document.getLong("totalQuestions")?.toInt() ?: 0
                        val correctAnswers = document.getLong("correctAnswers")?.toInt() ?: 0
                        val wrongAnswers = document.getLong("wrongAnswers")?.toInt() ?: 0
                        val quizType = document.getString("quizType") ?: "Unknown"
                        val difficulty = document.getString("difficulty") ?: "Unknown"

                        // Add data to the list
                        val historyData = HistoryDataClass(
                            "$category - $dateTime",
                            category,
                            score,
                            totalQuestions,
                            correctAnswers,
                            wrongAnswers,
                            quizType,
                            difficulty
                        )
                        historyList.add(historyData)
                    }
                    searchList.addAll(historyList)
                    historyAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.e("HistoryFragment", "Error fetching history: ${e.message}")
                }
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                searchList.clear()
                val searchText = newText?.lowercase(Locale.getDefault()).orEmpty()
                if (searchText.isNotEmpty()) {
                    historyList.forEach {
                        if (it.title.lowercase(Locale.getDefault()).contains(searchText)) {
                            searchList.add(it)
                        }
                    }
                } else {
                    searchList.addAll(historyList)
                }
                historyAdapter.notifyDataSetChanged()
                return false
            }
        })
    }
}
