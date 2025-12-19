package com.example.merjumaterjalid

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class EditWordActivity : AppCompatActivity() {

    private lateinit var wordSpinner: Spinner
    private lateinit var estonianWordInput: EditText
    private lateinit var questionInput: EditText
    private lateinit var exampleInput: EditText
    private lateinit var russianInput: EditText
    private lateinit var englishInput: EditText
    private lateinit var azerbaijaniInput: EditText
    private lateinit var saveButton: Button
    private lateinit var closeButton: Button
    
    private var allWords: MutableList<Word> = mutableListOf()
    private var selectedWordIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_word)

        // Initialize views
        wordSpinner = findViewById(R.id.wordSpinner)
        estonianWordInput = findViewById(R.id.editEstonianWordInput)
        questionInput = findViewById(R.id.editQuestionInput)
        exampleInput = findViewById(R.id.editExampleInput)
        russianInput = findViewById(R.id.editRussianInput)
        englishInput = findViewById(R.id.editEnglishInput)
        azerbaijaniInput = findViewById(R.id.editAzerbaijaniInput)
        saveButton = findViewById(R.id.editSaveButton)
        closeButton = findViewById(R.id.editCloseButton)

        // Load custom words
        loadCustomWords()

        if (allWords.isEmpty()) {
            Toast.makeText(this, "No custom words to edit", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Setup spinner
        val wordNames = allWords.map { it.name }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, wordNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        wordSpinner.adapter = spinnerAdapter

        wordSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedWordIndex = position
                if (selectedWordIndex >= 0 && selectedWordIndex < allWords.size) {
                    loadWordData(allWords[selectedWordIndex])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        closeButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            saveWord()
        }

        // Load first word by default
        if (allWords.isNotEmpty()) {
            loadWordData(allWords[0])
        }
    }

    private fun loadCustomWords() {
        try {
            val customWordsFile = File(filesDir, "custom_words.csv")
            if (customWordsFile.exists()) {
                customWordsFile.bufferedReader().use { reader ->
                    reader.forEachLine { line ->
                        val tokens = line.split(";")
                        if (tokens.size >= 6) {
                            allWords.add(
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
    }

    private fun loadWordData(word: Word) {
        estonianWordInput.setText(word.name)
        questionInput.setText(word.quest)
        exampleInput.setText(word.example)
        russianInput.setText(word.rus)
        englishInput.setText(word.eng)
        azerbaijaniInput.setText(word.aze)
    }

    private fun saveWord() {
        if (selectedWordIndex == -1) return

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

        // Update the word in the list
        allWords[selectedWordIndex] = Word(
            name = estonianWord,
            quest = question,
            example = example,
            rus = russian,
            eng = english,
            aze = azerbaijani
        )

        // Save all words back to file
        try {
            val customWordsFile = File(filesDir, "custom_words.csv")
            customWordsFile.bufferedWriter().use { writer ->
                allWords.forEach { word ->
                    val csvLine = "${word.name};${word.quest};${word.example};${word.rus};${word.eng};${word.aze}\n"
                    writer.write(csvLine)
                }
            }

            Toast.makeText(this, "Word updated successfully!", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error updating word: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
