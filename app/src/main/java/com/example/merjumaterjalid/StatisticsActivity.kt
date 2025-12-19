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
        val backButton: Button = findViewById(R.id.backButton)

        prefs = getSharedPreferences("MerjuPrefs", Context.MODE_PRIVATE)

        // Загружаем статистику
        val favorites = prefs.getStringSet("favorites", mutableSetOf()) ?: mutableSetOf()
        
        // Считаем общее количество слов
        val totalWords = countWordsInCsv()

        // Отображаем статистику
        totalWordsText.text = "Total Words: $totalWords"
        favoritesCountText.text = "Favorites: ${favorites.size}"

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun countWordsInCsv(): Int {
        var count = 0
        try {
            assets.open("words.csv").bufferedReader().use { reader ->
                reader.forEachLine { line ->
                    val tokens = line.split(";")
                    if (tokens.size >= 6) {
                        count++
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return count
    }
}
