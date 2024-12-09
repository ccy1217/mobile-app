package com.example.coursework

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapterClass(private val historyList: ArrayList<HistoryDataClass>) :
    RecyclerView.Adapter<HistoryAdapterClass.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.item_title)
        val correctAnswersTextView: TextView = itemView.findViewById(R.id.correct_answer)
        val wrongAnswersTextView: TextView = itemView.findViewById(R.id.wrong_answer)
        val totalQuestionsTextView: TextView = itemView.findViewById(R.id.total_questions)
        val quizTypeTextView: TextView = itemView.findViewById(R.id.quiz_type)
        val difficultyTextView: TextView = itemView.findViewById(R.id.difficulty)
        val itemIcon: ImageView = itemView.findViewById(R.id.item_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.history_item_layout, parent, false)
        return HistoryViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = historyList[position]
        holder.titleTextView.text = currentItem.title
        holder.correctAnswersTextView.text = "Correct: ${currentItem.correctAnswers}"
        holder.wrongAnswersTextView.text = "Wrong: ${currentItem.wrongAnswers}"
        holder.totalQuestionsTextView.text = "Total: ${currentItem.totalQuestions}"
        holder.quizTypeTextView.text = "Type: ${currentItem.quizType}"
        holder.difficultyTextView.text = "Difficulty: ${currentItem.difficulty}"

        // Map categories to drawable resources
        val categoryIconMap = mapOf(
            "General Knowledge" to R.drawable.general_knowledge,
            "Entertainment: Books" to R.drawable.entertainment_books,
            "Entertainment: Film" to R.drawable.entertainment_film,
            "Entertainment: Music" to R.drawable.entertainment_music,
            "Entertainment: Musicals & Theatres" to R.drawable.entertainment_musicals_theatres,
            "Entertainment: Televisions" to R.drawable.entertainment_televisions,
            "Entertainment: Video games" to R.drawable.entertainment_video_games,
            "Entertainment: Board Games" to R.drawable.entertainment_board_game,
            "Science & Nature" to R.drawable.science_nature,
            "Science: Computers" to R.drawable.science_computer,
            "Science: Mathematics" to R.drawable.science_mathematics,
            "Mythology" to R.drawable.mythology,
            "Sports" to R.drawable.sports,
            "Geography" to R.drawable.geography,
            "History" to R.drawable.history,
            "Politics" to R.drawable.politics,
            "Art" to R.drawable.art,
            "Celebrities" to R.drawable.celebrities,
            "Animals" to R.drawable.animals,
            "Vehicles" to R.drawable.vehicles,
            "Entertainment: Comics" to R.drawable.entertainment_comics,
            "Science: Gadgets" to R.drawable.science_gadgets,
            "Entertainment: Japanese Anime & Manga" to R.drawable.entertainment_japanese_anime_manga,
            "Entertainment: Cartoon & Animations" to R.drawable.entertainment_cartoon_animations
        )

        // Set the corresponding icon or default icon
        val iconRes = categoryIconMap[currentItem.category] ?: R.drawable.art
        holder.itemIcon.setImageResource(iconRes)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
}
