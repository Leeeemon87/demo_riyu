package com.example.myapplication.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.TaskStage1Activity
import com.example.myapplication.TaskStage2Activity
import com.example.myapplication.databinding.ItemExpandableGroupBinding
import com.example.myapplication.databinding.ItemSubitemBinding

class ExpandableListAdapter(private val items: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val VIEW_TYPE_GROUP = 0
        const val VIEW_TYPE_SUBITEM = 1
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_GROUP -> GroupViewHolder(ItemExpandableGroupBinding.inflate(inflater, parent, false))
            VIEW_TYPE_SUBITEM -> SubitemViewHolder(ItemSubitemBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is GroupItem -> (holder as GroupViewHolder).bind(item)
            is Subitem -> (holder as SubitemViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is GroupItem -> VIEW_TYPE_GROUP
            is Subitem -> VIEW_TYPE_SUBITEM
            else -> throw IllegalArgumentException("Invalid item type at position $position")
        }
    }

    // GroupViewHolder
//    inner class GroupViewHolder(private val binding: ItemExpandableGroupBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(groupItem: GroupItem) {
//            binding.groupTitleTextView.text = groupItem.title
//            binding.groupTitleTextView.setOnClickListener {
//                val subitemRecyclerView = binding.subitemRecyclerView
//                subitemRecyclerView.visibility = if (subitemRecyclerView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
//                notifyItemChanged(adapterPosition) // Notify adapter that data has changed
//            }
//            binding.subitemRecyclerView.adapter = SubitemAdapter(groupItem.subitems)
//        }
//
//    }
    inner class GroupViewHolder(private val binding: ItemExpandableGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(groupItem: GroupItem) {
            binding.groupTitleTextView.text = groupItem.title
            binding.subitemRecyclerView.visibility = if (groupItem.isExpanded) View.VISIBLE else View.GONE
            binding.groupTitleTextView.setOnClickListener {
                groupItem.isExpanded = !groupItem.isExpanded
                notifyItemChanged(adapterPosition) // Notify adapter that data has changed
//                Toast.makeText(binding.groupTitleTextView.context, groupItem.isExpanded.toString(), Toast.LENGTH_SHORT).show()
//                Toast.makeText(binding.groupTitleTextView.context, groupItem.subitems.toString(), Toast.LENGTH_SHORT).show()

            }
            // WARNING !
            // 使用 RecyclerView 的 adapter 前 一定要 LinearLayoutManager
            val recyclerView2 = binding.subitemRecyclerView

            // 设置布局管理器
            recyclerView2.layoutManager = LinearLayoutManager(binding.root.context)

            val spacingInPixels = binding.root.context.resources.getDimensionPixelSize(R.dimen.small_spacing) // 定义间隔大小，可以在 dimens.xml 中定义
            recyclerView2.addItemDecoration(SpaceItemDecoration(spacingInPixels))

            binding.subitemRecyclerView.adapter = SubitemAdapter(groupItem.subitems)
        }
    }

    // SubitemViewHolder
    inner class SubitemViewHolder(private val binding: ItemSubitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subitem: Subitem) {
            binding.textView.text = subitem.text
            binding.root.setOnClickListener {
                // Handle subitem click and navigate to a new activity
                val intent = Intent(itemView.context, TaskStage2Activity::class.java)
                itemView.context.startActivity(intent)
            }
        }
    }
}