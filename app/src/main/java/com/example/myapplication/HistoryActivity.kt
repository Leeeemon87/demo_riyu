package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import com.example.myapplication.databinding.ActivityHistoryBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ui.notifications.HistoryAdapter
import com.example.myapplication.ui.notifications.HistoryItem


class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter // 自定义的适配器

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 启用 ActionBar 的返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 设置返回按钮点击事件
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back) // 设置返回图标
        supportActionBar?.setHomeButtonEnabled(true)

        // 获取 RecyclerView
        recyclerView = binding.recyclerView

        // 设置布局管理器
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 创建适配器并设置给 RecyclerView
        historyAdapter = HistoryAdapter(getHistoryData()) // getHistoryData() 返回历史记录数据列表
        recyclerView.adapter = historyAdapter
    }

    // 这里可以根据您的数据结构自定义一个函数来获取历史记录数据
    private fun getHistoryData(): List<HistoryItem> {
        val folderPath = externalCacheDir?.absolutePath
        val historyItems = mutableListOf<HistoryItem>()
        folderPath?.let {
            val folderDirectory = File(it)
            if (folderDirectory.isDirectory) {
                val folderList = folderDirectory.listFiles()
                if (folderList != null) {
                    for (folder in folderList) {
                        if (folder.isDirectory) {
                            val timestamp = folder.name // 文件夹名称即为时间戳
                            Log.v("timestamp",timestamp)
                            // 获取录音内容文件名（不含扩展名）
                            val audioFileName = folder.list { dir, name ->
                                name.endsWith(".m4a")
                            }?.getOrNull(0)?.removeSuffix(".m4a")

                            if (!audioFileName.isNullOrEmpty()) {
                                val audioFilePath = "${folder.absolutePath}/$audioFileName.m4a"
                                val jsonFilePath = "${folder.absolutePath}/$audioFileName.json"
                                Log.v("fileGet",audioFilePath)
                                if (File(audioFilePath).exists() && File(jsonFilePath).exists()) {
                                    val historyItem = HistoryItem(audioFileName, audioFilePath, jsonFilePath, timestamp)
                                    historyItems.add(historyItem)
                                }
                            }
                        }
                    }
                }
            }
        }
        return historyItems
    }



    // 返回至上一个页面，而非主页
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // 点击 ActionBar 左上角的返回按钮
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
