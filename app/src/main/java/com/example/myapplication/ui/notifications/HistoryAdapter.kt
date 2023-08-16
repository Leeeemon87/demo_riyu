package com.example.myapplication.ui.notifications

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.PitchesActivity
import com.example.myapplication.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(private val historyData: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyItem = historyData[position]

        // 设置文本和点击事件
        holder.binding.historyText.text = historyItem.text
        val timestampLong = historyItem.timestamp.toLong() // 将字符串转换为长整型时间戳
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val formattedTimestamp = sdf.format(Date(timestampLong))

        holder.binding.timeStamp.text = formattedTimestamp
        holder.binding.microphoneIcon.setOnClickListener {
            // 在这里处理点击话筒图标的跳转逻辑，传递文件路径参数给 PitchesActivity
            val intent = Intent(holder.itemView.context, PitchesActivity::class.java)
            intent.putExtra("audioPath", historyItem.audioPath)
            intent.putExtra("jsonPath", historyItem.jsonPath)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return historyData.size
    }
}
