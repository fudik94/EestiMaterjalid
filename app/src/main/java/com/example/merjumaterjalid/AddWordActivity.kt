package com.example.merjumaterjalid

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class AddWordActivity : AppCompatActivity() {

    private lateinit var estonianWordInput: EditText
    private lateinit var questionInput: EditText
    private lateinit var exampleInput: EditText
    private lateinit var russianInput: EditText
    private lateinit var englishInput: EditText
    private lateinit var azerbaijaniInput: EditText
    private lateinit var saveButton: Button
    private lateinit var closeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_word)

        // Initialize views
        estonianWordInput = findViewById(R.id.estonianWordInput)
        questionInput = findViewById(R.id.questionInput)
        exampleInput = findViewById(R.id.exampleInput)
        russianInput = findViewById(R.id.russianInput)
        englishInput = findViewById(R.id.englishInput)
        azerbaijaniInput = findViewById(R.id.azerbaijaniInput)
        saveButton = findViewById(R.id.saveButton)
        closeButton = findViewById(R.id.closeButton)

        closeButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            saveWord()
        }
    }

    private fun saveWord() {
        val estonianWord = estonianWordInput.text.toString().trim()
        val question = questionInput.text.toString().trim()
        val example = exampleInput.text.toString().trim()
        val russian = russianInput.text.toString().trim()
        val english = englishInput.text.toString().trim()
        val azerbaijani = azerbaijaniInput.text.toString().trim()

        // Validate input
        if (estonianWord.isEmpty()) {
            Toast.makeText(this, "Please enter an Estonian word", Toast.LENGTH_SHORT).show()
            return
        }

        if (russian.isEmpty() && english.isEmpty() && azerbaijani.isEmpty()) {
            Toast.makeText(this, "Please enter at least one translation", Toast.LENGTH_SHORT).show()
            return
        }

        // Create CSV line
        val csvLine = "$estonianWord;$question;$example;$russian;$english;$azerbaijani\n"

        try {
            // Save to custom words file in internal storage
            val customWordsFile = File(filesDir, "custom_words.csv")
            FileOutputStream(customWordsFile, true).use { outputStream ->
                outputStream.write(csvLine.toByteArray())
            }

            Toast.makeText(this, "Word saved successfully!", Toast.LENGTH_SHORT).show()

            // Clear inputs
            estonianWordInput.text.clear()
            questionInput.text.clear()
            exampleInput.text.clear()
            russianInput.text.clear()
            englishInput.text.clear()
            azerbaijaniInput.text.clear()

            // Set result to notify MainActivity
            setResult(RESULT_OK)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving word: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
