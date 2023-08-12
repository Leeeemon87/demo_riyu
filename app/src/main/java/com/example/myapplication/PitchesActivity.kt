package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityPitchesBinding


class PitchesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPitchesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPitchesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView (view)

        // 启用 ActionBar 的返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 设置返回按钮点击事件
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back) // 设置返回图标
        supportActionBar?.setHomeButtonEnabled(true)



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
