package com.example.myapplication.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.TaskStage2Activity
import com.example.myapplication.databinding.ItemSubitemBinding

class SubitemAdapter(private val subitems: List<Subitem>) : RecyclerView.Adapter<SubitemAdapter.SubitemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubitemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        Toast.makeText(parent.context, "gggggggggggggggggg", Toast.LENGTH_SHORT)
        Toast.makeText(parent.context, subitems.toString(), Toast.LENGTH_LONG)

        val view = inflater.inflate(R.layout.item_subitem, parent, false)
        return SubitemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubitemViewHolder, position: Int) {
        val subitem = subitems[position]
        holder.bind(subitem)
    }

    override fun getItemCount(): Int = subitems.size

    inner class SubitemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(subitem: Subitem) {
            val textView = itemView.findViewById<TextView>(R.id.textView)
            textView.text = subitem.text

            itemView.setOnClickListener {
                // 处理子列表项点击事件，例如跳转到新的 Activity
                val intent = Intent(itemView.context, TaskStage2Activity::class.java)
                intent.putExtra("book", subitem.groupText)
                intent.putExtra("lesson", subitem.text)
                itemView.context.startActivity(intent)
            }
        }
    }
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubitemViewHolder {
//
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ItemSubitemBinding.inflate(inflater, parent, false)
//        return SubitemViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: SubitemViewHolder, position: Int) {
//        val subitem = subitems[position]
//        holder.bind(subitem)
//    }
//
//    override fun getItemCount(): Int = subitems.size
//
//    inner class SubitemViewHolder(private val binding: ItemSubitemBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(subitem: Subitem) {
//            binding.textView.text = subitem.text
//            notifyItemChanged(adapterPosition)
//            binding.root.setOnClickListener {
//                // Handle subitem click and navigate to a new activity
//                notifyItemChanged(adapterPosition)
//                val intent = Intent(itemView.context, TaskStage2Activity::class.java)
//                itemView.context.startActivity(intent)
//            }
//        }
//    }
}