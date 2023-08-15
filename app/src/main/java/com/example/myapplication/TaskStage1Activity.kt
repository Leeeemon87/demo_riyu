package com.example.myapplication

import android.os.Bundle
import android.view.MenuItem
import com.example.myapplication.ui.home.ExpandableListAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityTaskStage1Binding
import com.example.myapplication.ui.home.GroupItem
import com.example.myapplication.ui.home.SpaceItemDecoration
import com.example.myapplication.ui.home.Subitem
import org.json.JSONObject
import java.io.InputStream

class TaskStage1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskStage1Binding
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
        binding = ActivityTaskStage1Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 设置返回按钮点击事件
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back) // 设置返回图标
        supportActionBar?.setHomeButtonEnabled(true)

        val recyclerView = binding.expandableRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing) // 定义间隔大小，可以在 dimens.xml 中定义
        recyclerView.addItemDecoration(SpaceItemDecoration(spacingInPixels))


        val items = generateDummyData() // Create your own function to generate the data
        val adapter = ExpandableListAdapter(items)
        recyclerView.adapter = adapter
    }
    private fun generateDummyData(): List<GroupItem> {
        val groupItems = mutableListOf<GroupItem>()

        val jsonOri=loadJSONFromAsset("book.json")
        val jsonObject = JSONObject(jsonOri)
        val booksObject = jsonObject.getJSONObject("books")

        val iterator = booksObject.keys()
        while (iterator.hasNext()) {
            val subitems = mutableListOf<Subitem>()
            val key = iterator.next() as String
            val jsonTemp = booksObject.getJSONObject(key)

            val tempIter = jsonTemp.keys()
            while (tempIter.hasNext()) {
                val sub = tempIter.next() as String
                subitems.add(Subitem(sub, key))
            }

            groupItems.add(GroupItem(key, subitems))
            // 现在你可以在这里处理每个键值对
        }
//        for (i in 1..20) {
//            val subitems = mutableListOf<Subitem>()
//            for (j in 1..10) {
//                subitems.add(Subitem("Subitem $j of Group $i"))
//            }
////            Toast.makeText(binding.expandableRecyclerView.context, subitems.toString(), Toast.LENGTH_SHORT ).show()
//            groupItems.add(GroupItem("Group $i", subitems))
//        }

        return groupItems
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
