package com.example.myapplication.ui.notifications

class HistoryItem(val name: String, val audioPath: String, private val jsonPath: String, val timestamp: String) {
    val filePath:String =jsonPath
    // 构造函数和其他相关代码
    val text: String?=name
}
