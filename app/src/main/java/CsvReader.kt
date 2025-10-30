package com.example.merjumaterjalid

import android.content.Context

object CsvReader {
    fun readCsv(context: Context): MutableList<Word> {
        val list = mutableListOf<Word>()
        val inputStream = context.assets.open("words.csv")
        inputStream.bufferedReader().forEachLine { line ->
            val tokens = line.split(";")
            if (tokens.size == 6) {
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
        return list
    }
}
