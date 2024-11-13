package com.example.coursework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList
import android.widget.ImageView

class HomePageActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var imageList: Array<Int>
    private lateinit var titleList: Array<String>
    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<DataClass>
    private lateinit var adapter: AdapterClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Initialize data for images and titles
        imageList = arrayOf(R.drawable.animals, R.drawable.sports, R.drawable.geography,
            R.drawable.politics, R.drawable.music, R.drawable.maths, R.drawable.video_game,
            R.drawable.science_nature, R.drawable.computer, R.drawable.mythology)
        titleList = arrayOf("animals", "sports", "geography", "politics", "music", "maths",
            "video game", "science & nature", "computer", "mythology")

        // Set up RecyclerView and SearchView
        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.search)
        recyclerView.layoutManager = LinearLayoutManager(this)
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
                val searchText = newText?.toLowerCase(Locale.getDefault()).orEmpty()

                if (searchText.isNotEmpty()) {
                    dataList.forEach {
                        if (it.dataTitle.toLowerCase(Locale.getDefault()).contains(searchText)) {
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
        // Set up buttons for Home and Logout
        val homeIcon = findViewById<ImageView>(R.id.left_icon)
        val logoutIcon = findViewById<ImageView>(R.id.right_icon)

        // Navigate to HomePage1Activity when homeIcon is clicked
        homeIcon.setOnClickListener {
            val intent = Intent(this, MainHomePageActivity::class.java)
            startActivity(intent)
        }

        // Navigate to LoginPageActivity when logoutIcon is clicked
        logoutIcon.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java) // Replace with your actual login activity class
            startActivity(intent)
            finish()  // Close HomePageActivity to prevent going back after logging out
        }
    }

    private fun getData() {
        val scores = arrayOf(10, 20, 30, 35, 25, 50, 45, 55, 40, 15) // Example scores for each item
        for (i in imageList.indices) {
            val dataClass = DataClass(imageList[i], titleList[i], scores[i]) // Pass the score here
            dataList.add(dataClass)
        }
        searchList.addAll(dataList)
    }
}
