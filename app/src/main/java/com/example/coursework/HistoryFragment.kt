package com.example.coursework

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class HistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var imageList: Array<Int>
    private lateinit var titleList: Array<String>
    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<DataClass>
    private lateinit var adapter: AdapterClass

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        // Initialize data for images and titles
        imageList = arrayOf(R.drawable.animals, R.drawable.sports, R.drawable.geography)
        titleList = arrayOf("animals", "sports", "geography")

        // Set up RecyclerView and SearchView
        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.search)
        recyclerView.layoutManager = LinearLayoutManager(activity)  // Use 'activity' context for RecyclerView
        recyclerView.setHasFixedSize(true)

        // Initialize data lists
        dataList = arrayListOf()
        searchList = arrayListOf()
        getData()

        // Set adapter for the RecyclerView
        adapter = AdapterClass(searchList)
        recyclerView.adapter = adapter

        // Set up SearchView listener
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList.clear()
                val searchText = newText?.lowercase(Locale.getDefault()).orEmpty()

                if (searchText.isNotEmpty()) {
                    dataList.forEach {
                        if (it.dataTitle.lowercase(Locale.getDefault()).contains(searchText)) {
                            searchList.add(it)
                        }
                    }
                } else {
                    searchList.addAll(dataList)
                }
                adapter.notifyDataSetChanged()
                return false
            }
        })
        return view
    }

    private fun getData() {
        val scores = arrayOf(10, 20, 30) // Example scores for each item
        for (i in imageList.indices) {
            val dataClass = DataClass(imageList[i], titleList[i], scores[i]) // Pass the score here
            dataList.add(dataClass)
        }
        searchList.addAll(dataList)
    }
}
