package com.example.merjumaterjalid

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: WordAdapter
    private lateinit var allWords: MutableList<Word>
    private lateinit var favorites: MutableSet<String>
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val shuffleButton: Button = findViewById(R.id.shuffleButton)
        val favoritesButton: Button = findViewById(R.id.favoritesButton)
        val limitSpinner: Spinner = findViewById(R.id.limitSpinner)

        // Настраиваем SharedPreferences
        prefs = getSharedPreferences("MerjuPrefs", Context.MODE_PRIVATE)
        favorites = prefs.getStringSet("favorites", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        // Загружаем данные
        allWords = readCsv(this)

        // Создаём адаптер
        adapter = WordAdapter(
            allWords.toMutableList(),
            favorites
        ) { wordName, isFav ->
            if (isFav) favorites.add(wordName) else favorites.remove(wordName)
            prefs.edit().putStringSet("favorites", favorites).apply()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Настройка спиннера (10 / 20 / 30 / все)
        val limits = listOf("all", "10", "20", "30")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, limits)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        limitSpinner.adapter = spinnerAdapter

        limitSpinner.setSelection(0) // по умолчанию "Все"
        limitSpinner.setOnItemSelectedListener(SimpleItemSelectedListener { value ->
            val limit = when (value) {
                "10" -> 10
                "20" -> 20
                "30" -> 30
                else -> allWords.size
            }
            adapter.updateList(allWords.take(limit))
        })

        // Кнопка перемешать
        shuffleButton.setOnClickListener {
            allWords.shuffle()
            adapter.updateList(allWords)
        }

        // Кнопка Избранное
        favoritesButton.setOnClickListener {
            val favWords = allWords.filter { favorites.contains(it.name) }
            adapter.updateList(favWords)
        }
    }

    // Чтение CSV
    private fun readCsv(context: Context): MutableList<Word> {
        val list = mutableListOf<Word>()
        try {
            val inputStream = context.assets.open("words.csv")
            val reader = inputStream.bufferedReader()
            reader.forEachLine { line ->
                val tokens = line.split(";")
                if (tokens.size >= 6) {
                    list.add(
                        Word(
                            name = tokens[0],
                            quest = tokens[1],
                            example = tokens[2],
                            rus = tokens[3],
                            eng = tokens[4],
                            aze = tokens[5]
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
