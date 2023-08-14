package com.example.myapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.TaskStage1Activity
import com.example.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val rootView = binding.root

        binding.taskButton.setOnClickListener {
            // 在这里处理按钮点击事件
            // 创建一个 Intent，将当前 Fragment 的上下文和目标 Activity 类传递给它
            val intent = Intent(activity, TaskStage1Activity::class.java)
            startActivity(intent) // 启动目标 Activity
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}