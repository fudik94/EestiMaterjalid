package com.example.merjumaterjalid

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class DeleteWordActivity : AppCompatActivity() {

    private lateinit var wordSpinner: Spinner
    private lateinit var wordDetailsText: TextView
    private lateinit var deleteButton: Button
    private lateinit var closeButton: Button
    
    private var allWords: MutableList<Word> = mutableListOf()
    private var selectedWordIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_word)

        // Initialize views
        wordSpinner = findViewById(R.id.deleteWordSpinner)
        wordDetailsText = findViewById(R.id.wordDetailsText)
        deleteButton = findViewById(R.id.deleteWordButton)
        closeButton = findViewById(R.id.deleteCloseButton)

        // Load custom words
        loadCustomWords()

        if (allWords.isEmpty()) {
            Toast.makeText(this, "No custom words to delete", Toast.LENGTH_SHORT).show()
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
                    displayWordDetails(allWords[selectedWordIndex])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        closeButton.setOnClickListener {
            finish()
        }

        deleteButton.setOnClickListener {
            confirmAndDeleteWord()
        }

        // Display first word by default
        if (allWords.isNotEmpty()) {
            displayWordDetails(allWords[0])
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

    private fun displayWordDetails(word: Word) {
        val details = StringBuilder()
        details.append("🇪🇪 Estonian: ${word.name}\n\n")
        if (word.quest.isNotEmpty()) {
            details.append("❓ Question: ${word.quest}\n\n")
        }
        if (word.example.isNotEmpty()) {
            details.append("📝 Example: ${word.example}\n\n")
        }
        if (word.rus.isNotEmpty()) {
            details.append("🇷🇺 Russian: ${word.rus}\n\n")
        }
        if (word.eng.isNotEmpty()) {
            details.append("🇬🇧 English: ${word.eng}\n\n")
        }
        if (word.aze.isNotEmpty()) {
            details.append("🇦🇿 Azerbaijani: ${word.aze}")
        }
        wordDetailsText.text = details.toString()
    }

    private fun confirmAndDeleteWord() {
        if (selectedWordIndex == -1) return

        val wordToDelete = allWords[selectedWordIndex]

        AlertDialog.Builder(this)
            .setTitle("Delete Word")
            .setMessage("Are you sure you want to delete \"${wordToDelete.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                deleteWord()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteWord() {
        if (selectedWordIndex == -1) return

        // Remove the word from the list
        allWords.removeAt(selectedWordIndex)

        // Save remaining words back to file
        try {
            val customWordsFile = File(filesDir, "custom_words.csv")
            if (allWords.isEmpty()) {
                // Delete the file if no words left
                customWordsFile.delete()
                Toast.makeText(this, "Word deleted. No more custom words.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                customWordsFile.bufferedWriter().use { writer ->
                    allWords.forEach { word ->
                        val csvLine = "${word.name};${word.quest};${word.example};${word.rus};${word.eng};${word.aze}\n"
                        writer.write(csvLine)
                    }
                }

                Toast.makeText(this, "Word deleted successfully!", Toast.LENGTH_SHORT).show()

                // Update spinner
                val wordNames = allWords.map { it.name }
                val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, wordNames)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                wordSpinner.adapter = spinnerAdapter

                // Select first word if available
                if (allWords.isNotEmpty()) {
                    selectedWordIndex = 0
                    displayWordDetails(allWords[0])
                }

                setResult(RESULT_OK)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error deleting word: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
