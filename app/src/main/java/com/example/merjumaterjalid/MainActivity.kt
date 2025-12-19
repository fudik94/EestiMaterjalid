package com.example.merjumaterjalid

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: WordAdapter
    private lateinit var allWords: MutableList<Word>
    private lateinit var displayedWords: MutableList<Word>
    private lateinit var favorites: MutableSet<String>
    private lateinit var prefs: SharedPreferences
    private var currentLimit: Int = Int.MAX_VALUE
    private var isShowingFavorites: Boolean = false
    private val WORD_LIBRARY_REQUEST_CODE = 1
    private var isMenuExpanded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val shuffleButton: Button = findViewById(R.id.shuffleButton)
        val favoritesButton: Button = findViewById(R.id.favoritesButton)
        val showAllButton: Button = findViewById(R.id.showAllButton)
        val statisticsButton: Button = findViewById(R.id.statisticsButton)
        val limitSpinner: Spinner = findViewById(R.id.limitSpinner)
        val searchEditText: EditText = findViewById(R.id.searchEditText)
        val menuToggleButton: Button = findViewById(R.id.menuToggleButton)
        val collapsibleMenuContainer: LinearLayout = findViewById(R.id.collapsibleMenuContainer)

        // Настраиваем SharedPreferences
        prefs = getSharedPreferences("MerjuPrefs", Context.MODE_PRIVATE)
        favorites = prefs.getStringSet("favorites", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        // Загружаем данные
        allWords = readCsv(this)
        displayedWords = allWords.toMutableList()

        // Создаём адаптер
        adapter = WordAdapter(
            displayedWords,
            favorites
        ) { wordName, isFav ->
            if (isFav) favorites.add(wordName) else favorites.remove(wordName)
            prefs.edit().putStringSet("favorites", favorites).apply()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Menu Toggle Button
        menuToggleButton.setOnClickListener {
            toggleMenu(collapsibleMenuContainer, menuToggleButton)
        }

        // Настройка спиннера (10 / 20 / 30 / все)
        val limits = listOf("All", "10", "20", "30", "50")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, limits)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        limitSpinner.adapter = spinnerAdapter

        limitSpinner.setSelection(0) // по умолчанию "Все"
        limitSpinner.setOnItemSelectedListener(SimpleItemSelectedListener { value ->
            currentLimit = when (value) {
                "10" -> 10
                "20" -> 20
                "30" -> 30
                "50" -> 50
                else -> Int.MAX_VALUE
            }
            updateDisplayedWords()
        })

        // Поиск слов
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                filterWords(query)
            }
        })

        // Кнопка перемешать
        shuffleButton.setOnClickListener {
            displayedWords.shuffle()
            adapter.updateList(displayedWords)
        }

        // Кнопка Избранное
        favoritesButton.setOnClickListener {
            isShowingFavorites = true
            updateDisplayedWords()
        }

        // Кнопка Показать все
        showAllButton.setOnClickListener {
            isShowingFavorites = false
            searchEditText.text.clear()
            updateDisplayedWords()
        }

        // Кнопка Статистика
        statisticsButton.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }

        // Кнопка Квиз
        val quizButton: Button = findViewById(R.id.quizButton)
        quizButton.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        // Кнопка Word Library
        val wordLibraryButton: Button = findViewById(R.id.wordLibraryButton)
        wordLibraryButton.setOnClickListener {
            startActivityForResult(Intent(this, WordLibraryActivity::class.java), WORD_LIBRARY_REQUEST_CODE)
        }
    }

    private fun toggleMenu(menuContainer: LinearLayout, toggleButton: Button) {
        if (isMenuExpanded) {
            // Collapse menu
            menuContainer.visibility = View.GONE
            toggleButton.text = getString(R.string.menu_toggle_button)
            isMenuExpanded = false
        } else {
            // Expand menu
            menuContainer.visibility = View.VISIBLE
            toggleButton.text = "▼ Close Menu"
            isMenuExpanded = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WORD_LIBRARY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Reload words when a word is added/edited/deleted
            allWords = readCsv(this)
            updateDisplayedWords()
        }
    }

    // Фильтрация слов по поисковому запросу
    private fun filterWords(query: String) {
        if (query.isEmpty()) {
            updateDisplayedWords()
            return
        }

        val filtered = if (isShowingFavorites) {
            allWords.filter { favorites.contains(it.name) && it.name.lowercase().contains(query) }
        } else {
            allWords.filter { it.name.lowercase().contains(query) ||
                    it.rus.lowercase().contains(query) ||
                    it.eng.lowercase().contains(query) ||
                    it.aze.lowercase().contains(query) }
        }
        
        displayedWords = filtered.take(currentLimit).toMutableList()
        adapter.updateList(displayedWords)
    }

    // Обновление отображаемых слов
    private fun updateDisplayedWords() {
        displayedWords = if (isShowingFavorites) {
            val favWords = allWords.filter { favorites.contains(it.name) }
            if (currentLimit == Int.MAX_VALUE) {
                favWords.toMutableList()
            } else {
                favWords.take(currentLimit).toMutableList()
            }
        } else {
            if (currentLimit == Int.MAX_VALUE) {
                allWords.toMutableList()
            } else {
                allWords.take(currentLimit).toMutableList()
            }
        }
        adapter.updateList(displayedWords)
    }

    // Чтение CSV
    private fun readCsv(context: Context): MutableList<Word> {
        val list = mutableListOf<Word>()
        
        // Load words from assets
        try {
            context.assets.open("words.csv").bufferedReader().use { reader ->
                reader.forEachLine { line ->
                    val tokens = line.split(";")
                    if (tokens.size >= 6) {
                        list.add(
                            Word(
                                name = tokens[0].trim(),
                                quest = tokens[1].trim(),
                                example = tokens[2].trim(),
                                rus = tokens[3].trim(),
                                eng = tokens[4].trim(),
                                aze = tokens[5].trim()
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Load custom words from internal storage
        try {
            val customWordsFile = java.io.File(filesDir, "custom_words.csv")
            if (customWordsFile.exists()) {
                customWordsFile.bufferedReader().use { reader ->
                    reader.forEachLine { line ->
                        val tokens = line.split(";")
                        if (tokens.size >= 6) {
                            list.add(
                                Word(
                                    name = tokens[0].trim(),
                                    quest = tokens[1].trim(),
                                    example = tokens[2].trim(),
                                    rus = tokens[3].trim(),
                                    eng = tokens[4].trim(),
                                    aze = tokens[5].trim()
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return list
    }
}
