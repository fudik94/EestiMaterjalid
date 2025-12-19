package com.example.merjumaterjalid

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {

    private lateinit var allWords: MutableList<Word>
    private lateinit var currentWord: Word
    private var currentIndex = 0
    private var correctAnswers = 0
    private var totalQuestions = 0
    private var selectedLanguage = "rus" // Default to Russian

    private lateinit var questionText: TextView
    private lateinit var scoreText: TextView
    private lateinit var optionsContainer: LinearLayout
    private lateinit var nextButton: Button
    private lateinit var finishButton: Button
    private lateinit var resultText: TextView
    private lateinit var exitButton: Button
    private lateinit var languageSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        questionText = findViewById(R.id.questionText)
        scoreText = findViewById(R.id.scoreText)
        optionsContainer = findViewById(R.id.optionsContainer)
        nextButton = findViewById(R.id.nextButton)
        finishButton = findViewById(R.id.finishButton)
        resultText = findViewById(R.id.resultText)
        exitButton = findViewById(R.id.exitButton)
        languageSpinner = findViewById(R.id.languageSpinner)

        // Setup language spinner
        val languages = listOf("🇷🇺 Russian", "🇬🇧 English", "🇦🇿 Azerbaijani")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = spinnerAdapter
        languageSpinner.setSelection(0) // Default to Russian

        languageSpinner.setOnItemSelectedListener(SimpleItemSelectedListener { value ->
            selectedLanguage = when (value) {
                "🇬🇧 English" -> "eng"
                "🇦🇿 Azerbaijani" -> "aze"
                else -> "rus"
            }
            // Reset quiz when language changes
            currentIndex = 0
            correctAnswers = 0
            totalQuestions = 0
            allWords.shuffle()
            showNextQuestion()
        })

        // Exit button handler
        exitButton.setOnClickListener {
            finish()
        }

        // Загружаем слова
        allWords = readCsv(this)
        
        // Проверяем, что есть достаточно слов для квиза
        if (allWords.size < 2) {
            questionText.text = "Not enough words for quiz"
            resultText.text = "Please add more words to the database"
            resultText.visibility = View.VISIBLE
            finishButton.visibility = View.VISIBLE
            finishButton.setOnClickListener { finish() }
            return
        }
        
        allWords.shuffle()

        // Начинаем квиз
        showNextQuestion()

        nextButton.setOnClickListener {
            showNextQuestion()
        }

        finishButton.setOnClickListener {
            finish()
        }
    }

    private fun showNextQuestion() {
        if (currentIndex >= allWords.size) {
            showResults()
            return
        }

        resultText.visibility = View.GONE
        nextButton.visibility = View.GONE
        optionsContainer.removeAllViews()

        currentWord = allWords[currentIndex]
        totalQuestions++

        // Показываем вопрос
        questionText.text = "What is the translation of:\n\"${currentWord.name}\"?"
        scoreText.text = "Score: $correctAnswers / $totalQuestions"

        // Get correct answer based on selected language
        val correctAnswer = when (selectedLanguage) {
            "eng" -> currentWord.eng
            "aze" -> currentWord.aze
            else -> currentWord.rus
        }

        // Создаем варианты ответов
        val options = mutableListOf(correctAnswer)
        
        // Добавляем случайные неправильные ответы (избегаем дубликатов)
        val otherWords = allWords.filter { it.name != currentWord.name }.shuffled()
        for (word in otherWords) {
            if (options.size >= 4) break
            val wrongAnswer = when (selectedLanguage) {
                "eng" -> word.eng
                "aze" -> word.aze
                else -> word.rus
            }
            if (!options.contains(wrongAnswer) && wrongAnswer.isNotEmpty()) {
                options.add(wrongAnswer)
            }
        }
        
        // Если недостаточно уникальных вариантов, добавляем из других языков
        if (options.size < 4) {
            for (word in otherWords) {
                if (options.size >= 4) break
                // Try other languages as fallback
                val fallbackOptions = listOf(word.rus, word.eng, word.aze)
                for (fallback in fallbackOptions) {
                    if (options.size >= 4) break
                    if (!options.contains(fallback) && fallback.isNotEmpty()) {
                        options.add(fallback)
                    }
                }
            }
        }
        
        options.shuffle()

        // Создаем кнопки для вариантов
        options.forEach { option ->
            val button = Button(this)
            button.text = option
            button.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
            
            button.setOnClickListener {
                checkAnswer(option, button)
            }
            
            optionsContainer.addView(button)
        }

        currentIndex++
    }

    private fun checkAnswer(selectedAnswer: String, button: Button) {
        val correctAnswer = when (selectedLanguage) {
            "eng" -> currentWord.eng
            "aze" -> currentWord.aze
            else -> currentWord.rus
        }
        val isCorrect = selectedAnswer == correctAnswer
        
        if (isCorrect) {
            correctAnswers++
            button.setBackgroundColor(getColor(android.R.color.holo_green_light))
            resultText.text = "✅ Correct!"
            resultText.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            button.setBackgroundColor(getColor(android.R.color.holo_red_light))
            resultText.text = "❌ Wrong! Correct answer: $correctAnswer"
            resultText.setTextColor(getColor(android.R.color.holo_red_dark))
        }

        // Отключаем все кнопки
        for (i in 0 until optionsContainer.childCount) {
            optionsContainer.getChildAt(i).isEnabled = false
        }

        resultText.visibility = View.VISIBLE
        nextButton.visibility = View.VISIBLE
        scoreText.text = "Score: $correctAnswers / $totalQuestions"
    }

    private fun showResults() {
        questionText.text = "Quiz Complete! 🎉"
        
        // Guard against division by zero
        if (totalQuestions == 0) {
            scoreText.text = "No questions answered"
            resultText.text = "Please try again"
            resultText.setTextColor(getColor(android.R.color.holo_orange_dark))
        } else {
            scoreText.text = "Final Score: $correctAnswers / $totalQuestions"
            
            val percentage = (correctAnswers.toFloat() / totalQuestions * 100).toInt()
            resultText.text = when {
                percentage >= 90 -> "🌟 Excellent! $percentage%"
                percentage >= 70 -> "👍 Good job! $percentage%"
                percentage >= 50 -> "👌 Not bad! $percentage%"
                else -> "💪 Keep practicing! $percentage%"
            }
            resultText.setTextColor(getColor(android.R.color.holo_blue_dark))
        }
        
        optionsContainer.removeAllViews()
        nextButton.visibility = View.GONE
        finishButton.visibility = View.VISIBLE
        resultText.visibility = View.VISIBLE
    }

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
