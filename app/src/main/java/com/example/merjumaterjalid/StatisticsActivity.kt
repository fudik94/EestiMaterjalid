package com.example.merjumaterjalid

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StatisticsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val totalWordsText: TextView = findViewById(R.id.totalWordsText)
        val favoritesCountText: TextView = findViewById(R.id.favoritesCountText)
        val wordsViewedText: TextView = findViewById(R.id.wordsViewedText)
        val backButton: Button = findViewById(R.id.backButton)

        prefs = getSharedPreferences("MerjuPrefs", Context.MODE_PRIVATE)

        // Загружаем статистику
        val favorites = prefs.getStringSet("favorites", mutableSetOf()) ?: mutableSetOf()
        val wordsViewed = prefs.getInt("words_viewed", 0)
        
        // Считаем общее количество слов
        val totalWords = countWordsInCsv()

        // Отображаем статистику
        totalWordsText.text = "Total Words: $totalWords"
        favoritesCountText.text = "Favorites: ${favorites.size}"
        wordsViewedText.text = "Words Viewed: $wordsViewed"

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun countWordsInCsv(): Int {
        var count = 0
        try {
            val inputStream = assets.open("words.csv")
            val reader = inputStream.bufferedReader()
            reader.forEachLine { line ->
                val tokens = line.split(";")
                if (tokens.size >= 6) {
                    count++
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return count
    }
}
