package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.databinding.ActivityPitchesBinding


class PitchesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPitchesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPitchesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView (view)

        // 设置 Toolbar
//        val toolbar: Toolbar = binding.toolbar

        // 启用返回按钮
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        toolbar.setNavigationOnClickListener {
//            // 处理返回按钮点击事件
//            onBackPressed()
//        }


    }
}
