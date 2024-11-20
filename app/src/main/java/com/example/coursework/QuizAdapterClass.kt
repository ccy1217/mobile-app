package com.example.coursework

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuizAdapterClass(
    private val quizList: List<QuizDataClass>,
    private val onAnswerSelected: (Boolean) -> Unit
) : RecyclerView.Adapter<QuizAdapterClass.QuizViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.questions_item_layout, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quizData = quizList[position]
        holder.bind(quizData, onAnswerSelected)
    }

    override fun getItemCount() = quizList.size

    class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTextView: TextView = itemView.findViewById(R.id.questions)
        private val choicesRadioGroup: RadioGroup = itemView.findViewById(R.id.questions_choices)

        fun bind(quizData: QuizDataClass, onAnswerSelected: (Boolean) -> Unit) {
            questionTextView.text = quizData.question
            choicesRadioGroup.removeAllViews()

            val allChoices = quizData.getAllAnswers().shuffled()
            allChoices.forEach { choice ->
                val radioButton = RadioButton(itemView.context).apply {
                    text = choice
                    id = View.generateViewId()
                }
                radioButton.setOnClickListener {
                    onAnswerSelected(choice == quizData.correctAnswer)
                }
                choicesRadioGroup.addView(radioButton)
            }
        }
    }
}