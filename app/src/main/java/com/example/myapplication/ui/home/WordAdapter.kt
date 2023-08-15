package com.example.myapplication.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.WordActivity

class WordAdapter(private val wordList: List<WordItem>) : RecyclerView.Adapter<WordAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wordItem = wordList[position]
        holder.bind(wordItem) // Bind data to ViewHolder
    }

    override fun getItemCount(): Int {
        return wordList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val wordTextView: TextView = itemView.findViewById(R.id.wordTextView)
        private val toneTextView: TextView = itemView.findViewById(R.id.toneTextView)
        private val kanaTextView: TextView = itemView.findViewById(R.id.kanaTextView)

        fun bind(wordItem: WordItem) {
            wordTextView.text = wordItem.word
            toneTextView.text = wordItem.tone
            kanaTextView.text = wordItem.kana

            itemView.setOnClickListener {
                // Handle item click here
                Toast.makeText(itemView.context, "succeed in jumping", Toast.LENGTH_SHORT).show()

//                 You can also start WordActivity here
                val intent = Intent(itemView.context, WordActivity::class.java).apply {
                    putExtra("honmei", wordItem.word)
                    putExtra("accent", wordItem.tone)
                    putExtra("furikana", wordItem.kana)
                }
                itemView.context.startActivity(intent)
            }
        }
    }
}
