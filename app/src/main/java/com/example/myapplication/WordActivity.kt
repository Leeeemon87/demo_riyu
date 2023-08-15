package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import com.example.myapplication.databinding.ActivityWordBinding
import com.example.myapplication.ui.home.KanaItem
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


class WordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 启用 ActionBar 的返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 设置返回按钮点击事件
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back) // 设置返回图标
        supportActionBar?.setHomeButtonEnabled(true)

        val honmei=intent.getStringExtra("honmei")
        val furikana = intent.getStringExtra("furikana")
        val accent=intent.getStringExtra("accent")

        // 判断参数是否为非空，如果都不为空才创建 KanaItem 实例
        if (honmei != null && furikana != null && accent != null) {
            // 创建 KanaItem 实例
            val kanaItem = KanaItem(honmei, furikana, accent)

            val meanChart: LineChart = binding.accentChart
            val lineDataSets = mutableListOf<ILineDataSet>() // 用于存储所有线的数据集

            for (i in 0 until kanaItem.n) {
                val hiraName = kanaItem.furikana[i]
                val x1 = i*2F
                val y1 = kanaItem.acs[i].toFloat()
                val x2 = i*2+1F
                val y2 = kanaItem.acs[i].toFloat()

                val lineEntries = mutableListOf<Entry>()
                lineEntries.add(Entry(x1, y1))
                lineEntries.add(Entry(x2, y2))

                val lineDataSet = LineDataSet(lineEntries,furikana[i].toString())
                lineDataSet.lineWidth = 5f

                val lineColor = Color.GRAY
                lineDataSet.color = lineColor
                lineDataSet.setDrawFilled(false)
                // 设置数据点颜色
                lineDataSet.setCircleColor(lineColor) // 设置为所需的颜色
                // 禁用数据点的值显示，并设置其格式
                lineDataSet.setDrawValues(false)
                lineDataSet.setDrawCircles(false)
                // 设置数据点为实心
                lineDataSet.setDrawCircleHole(false)
                // 设置数据点半径（大小）
                lineDataSet.circleRadius = 2.5f // 设置为所需的半径值

                lineDataSets.add(lineDataSet)
            }

            val meanData = LineData(lineDataSets)
            meanChart.data = meanData

            // 设置图表外观和行为
            meanChart.description.isEnabled = false
            meanChart.legend.isEnabled = false
            meanChart.xAxis.isEnabled = false
            meanChart.axisLeft.isEnabled = false
            meanChart.axisRight.isEnabled = false
            meanChart.xAxis.setDrawLabels(false)
            meanChart.xAxis.setDrawGridLines(false) // 不绘制 X 轴上的网格线
            meanChart.axisLeft.setDrawGridLines(false) // 不绘制左侧 Y 轴上的网格线
            meanChart.axisRight.setDrawGridLines(false) // 不绘制右侧 Y 轴上的网格线

            // 刷新图表
            meanChart.invalidate()
        }
        else{
            onBackPressed()
        }

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
