package com.example.coursework

data class QuizDataClass(
    val question: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
) {
    // Combine correct answer and incorrect answers into one list of choices
    fun getAllAnswers(): List<String> {
        return incorrectAnswers + correctAnswer
    }
}
