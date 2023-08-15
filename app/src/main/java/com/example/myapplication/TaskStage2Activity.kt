package com.example.myapplication

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityTaskStage1Binding
import com.example.myapplication.databinding.ActivityTaskStage2Binding
import com.example.myapplication.ui.home.ExpandableListAdapter
import com.example.myapplication.ui.home.GroupItem
import com.example.myapplication.ui.home.Subitem
import com.example.myapplication.ui.home.WordAdapter
import com.example.myapplication.ui.home.WordItem
import org.json.JSONObject
import java.io.InputStream

class TaskStage2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskStage2Binding
    private lateinit var wordsRecyclerView: RecyclerView
    private lateinit var wordAdapter: WordAdapter
    private lateinit var wordItemList: MutableList<WordItem>

    private fun loadJSONFromAsset(fileName: String): String {
        val inputStream: InputStream = assets.open(fileName)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charsets.UTF_8)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 设置返回按钮点击事件
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back) // 设置返回图标
        supportActionBar?.setHomeButtonEnabled(true)

        binding = ActivityTaskStage2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val textView = findViewById<TextView>(R.id.textView)
        textView.text = "待开发"

        setContentView(R.layout.activity_task_stage2)

        wordsRecyclerView = findViewById(R.id.wordsRecyclerView)

        wordItemList = mutableListOf()
        // 添加示例数据
//        wordItemList.add(WordItem("今日は", "0", "こんにちは"))
//
//        wordItemList.add(WordItem("死にたい", "3", "しにたい"))
        // 添加更多单词

        val book = intent.getStringExtra("book")
        val lesson = intent.getStringExtra("lesson")

        val jsonOri=loadJSONFromAsset("book.json")
        val jsonObject = JSONObject(jsonOri)

        val booksObject = jsonObject.getJSONObject("books")

        val lessonsObject = booksObject.getJSONObject(book)

        val wordArray = lessonsObject.getJSONArray(lesson)

        for (j in 0 until wordArray.length()) {
            val wordObject = wordArray.getJSONObject(j)
            val kana = wordObject.getString("kana")
            val word = wordObject.getString("word")
            val tone = wordObject.getString("tone")
            wordItemList.add(WordItem(word, tone, kana))
        }








        wordAdapter = WordAdapter(wordItemList)
        wordsRecyclerView.adapter = wordAdapter

        val layoutManager = LinearLayoutManager(this)
        wordsRecyclerView.layoutManager = layoutManager

    }
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