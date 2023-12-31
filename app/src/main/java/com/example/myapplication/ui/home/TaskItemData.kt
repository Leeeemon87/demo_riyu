package com.example.myapplication.ui.home

data class GroupItem(val title: String, val subitems: List<Subitem>, var isExpanded: Boolean = false)
data class Subitem(val text: String, val groupText: String)

data class WordItem(val word: String, val tone: String, val kana: String)
