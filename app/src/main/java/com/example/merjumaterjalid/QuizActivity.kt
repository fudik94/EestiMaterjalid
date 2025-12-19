package com.example.merjumaterjalid

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {

    private lateinit var allWords: MutableList<Word>
    private lateinit var currentWord: Word
    private var currentIndex = 0
    private var correctAnswers = 0
    private var totalQuestions = 0

    private lateinit var questionText: TextView
    private lateinit var scoreText: TextView
    private lateinit var optionsContainer: LinearLayout
    private lateinit var nextButton: Button
    private lateinit var finishButton: Button
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        questionText = findViewById(R.id.questionText)
        scoreText = findViewById(R.id.scoreText)
        optionsContainer = findViewById(R.id.optionsContainer)
        nextButton = findViewById(R.id.nextButton)
        finishButton = findViewById(R.id.finishButton)
        resultText = findViewById(R.id.resultText)

        // Загружаем слова
        allWords = readCsv(this)
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

        // Создаем варианты ответов
        val options = mutableListOf(currentWord.rus)
        
        // Добавляем случайные неправильные ответы
        val otherWords = allWords.filter { it.name != currentWord.name }.shuffled()
        val numOptions = minOf(3, otherWords.size)
        for (i in 0 until numOptions) {
            options.add(otherWords[i].rus)
        }
        
        // Если недостаточно уникальных вариантов, дублируем с другими переводами
        if (options.size < 4 && otherWords.isNotEmpty()) {
            for (word in otherWords) {
                if (options.size >= 4) break
                if (!options.contains(word.eng) && word.eng.isNotEmpty()) {
                    options.add(word.eng)
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
        val isCorrect = selectedAnswer == currentWord.rus
        
        if (isCorrect) {
            correctAnswers++
            button.setBackgroundColor(getColor(android.R.color.holo_green_light))
            resultText.text = "✅ Correct!"
            resultText.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            button.setBackgroundColor(getColor(android.R.color.holo_red_light))
            resultText.text = "❌ Wrong! Correct answer: ${currentWord.rus}"
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
        try {
            val inputStream = context.assets.open("words.csv")
            val reader = inputStream.bufferedReader()
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
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
