package com.example.merjumaterjalid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WordLibraryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_library)

        val createWordButton: Button = findViewById(R.id.createWordButton)
        val editWordButton: Button = findViewById(R.id.editWordButton)
        val deleteWordButton: Button = findViewById(R.id.deleteWordButton)
        val closeButton: Button = findViewById(R.id.closeLibraryButton)

        createWordButton.setOnClickListener {
            startActivity(Intent(this, AddWordActivity::class.java))
        }

        editWordButton.setOnClickListener {
            startActivity(Intent(this, EditWordActivity::class.java))
        }

        deleteWordButton.setOnClickListener {
            startActivity(Intent(this, DeleteWordActivity::class.java))
        }

        closeButton.setOnClickListener {
            finish()
        }
    }
}
