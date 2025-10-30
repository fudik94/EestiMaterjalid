package com.example.merjumaterjalid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordAdapter(
    private var words: MutableList<Word>,
    private val favorites: MutableSet<String>,
    private val onFavoriteChanged: (String, Boolean) -> Unit
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.wordName)
        val translation: TextView = itemView.findViewById(R.id.wordTranslation)
        val quest: TextView = itemView.findViewById(R.id.wordQuest)
        val example: TextView = itemView.findViewById(R.id.wordExample)
        val toggleTranslation: Button = itemView.findViewById(R.id.toggleTranslation)
        val toggleQuest: Button = itemView.findViewById(R.id.toggleQuest)
        val toggleExample: Button = itemView.findViewById(R.id.toggleExample)
        val favoriteButton: Button = itemView.findViewById(R.id.favoriteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]

        holder.name.text = word.name
        holder.translation.text = "🇷🇺 ${word.rus}\n🇬🇧 ${word.eng}\n🇦🇿 ${word.aze}"
        holder.quest.text = word.quest
        holder.example.text = word.example

        holder.translation.visibility = View.GONE
        holder.quest.visibility = View.GONE
        holder.example.visibility = View.GONE

        val isFavorite = favorites.contains(word.name)
        holder.favoriteButton.text = if (isFavorite) "✅" else "⭐"

        holder.favoriteButton.setOnClickListener {
            val nowFav = !favorites.contains(word.name)
            onFavoriteChanged(word.name, nowFav)
            holder.favoriteButton.text = if (nowFav) "✅" else "⭐"
        }

        holder.toggleTranslation.setOnClickListener {
            holder.translation.visibility =
                if (holder.translation.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        holder.toggleQuest.setOnClickListener {
            holder.quest.visibility =
                if (holder.quest.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        holder.toggleExample.setOnClickListener {
            holder.example.visibility =
                if (holder.example.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    override fun getItemCount() = words.size

    fun updateList(newList: List<Word>) {
        words.clear()
        words.addAll(newList)
        notifyDataSetChanged()
    }

    fun shuffleList() {
        words.shuffle()
        notifyDataSetChanged()
    }
}
