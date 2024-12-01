package com.example.coursework

data class HistoryDataClass(
    val title: String,  //Category + DateTime
    val category: String,
    val score: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val quizType: String,
    val difficulty: String
)

