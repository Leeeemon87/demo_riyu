package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityTaskStage1Binding
import com.example.myapplication.ui.home.ExpandableListAdapter
import com.example.myapplication.ui.home.GroupItem
import com.example.myapplication.ui.home.Subitem

class TaskStage2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskStage1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskStage1Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val recyclerView = binding.expandableRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val items = generateDummyData() // Create your own function to generate the data
        val adapter = ExpandableListAdapter(items)
        recyclerView.adapter = adapter
    }
    private fun generateDummyData(): List<GroupItem> {
        val groupItems = mutableListOf<GroupItem>()

        for (i in 1..20) {
            val subitems = mutableListOf<Subitem>()
            for (j in 1..10) {
                subitems.add(Subitem("Subitem $j of Group $i"))
            }
//            Toast.makeText(binding.expandableRecyclerView.context, subitems.toString(), Toast.LENGTH_SHORT ).show()
            groupItems.add(GroupItem("Group $i", subitems))
        }

        return groupItems
    }
}