package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityPitchesBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream


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

        val jsonOri = loadJSONFromAsset("result.json")

        val jsonObject = JSONObject(jsonOri)
        val jsondata = jsonObject.getJSONObject("data")

        val listHira=jsondata.getJSONArray("hira")
        val listStarts=jsondata.getJSONArray("start")
        val listEnds=jsondata.getJSONArray("end")
        val jsonPitch=jsondata.getJSONObject("pitch")
        val listMean=jsonPitch.getJSONArray("mean")
        val listAllValue=jsonPitch.getJSONArray("allValue")
        val listAllTime=jsonPitch.getJSONArray("allTime")

        Log.v("sdd",listHira.toString())
        Log.v("sdd",listStarts.toString())
        Log.v("sdd",listEnds.toString())
        Log.v("sdd",listMean.toString())
        Log.v("sdd",listAllValue.toString())
        Log.v("sdd",listAllTime.toString())

        val entries = mutableListOf<Entry>()

        for (i in 0 until listAllTime.length()) {
            val x = listAllTime.getDouble(i).toFloat() // 提取 x 坐标数据
            val y = listAllValue.getDouble(i).toFloat() // 提取 y 坐标数据
            entries.add(Entry(x, y))
        }

        val dataSet = LineDataSet(entries, "Label")
        val lineData = LineData(dataSet)

        val waveformChart: LineChart = findViewById(R.id.waveformChart)
        // Set the LineData to the chart
        waveformChart.data = lineData

        // Customize chart appearance and behavior as needed
        waveformChart.description.isEnabled = true
        waveformChart.axisLeft.isEnabled = true
        waveformChart.axisRight.isEnabled = true
        waveformChart.xAxis.isEnabled = true
        waveformChart.legend.isEnabled = true

        waveformChart.invalidate() // Refresh the chart

        // 创建 BarChart 实例
        val barChart: BarChart = binding.barChart

        // 创建柱状图数据集
        val barEntries = mutableListOf<BarEntry>()

// 添加柱状图数据
        for (i in 0 until listAllTime.length()) {
            val x = listAllTime.getDouble(i).toFloat() // 提取 x 坐标数据
            val y = listAllValue.getDouble(i).toFloat() // 提取 y 坐标数据
            barEntries.add(BarEntry(x, y))
        }

// 创建柱状图数据集
        val barDataSet = BarDataSet(barEntries, "BarDataSet")
        val barData = BarData(barDataSet)

// 设置柱状图数据
        barChart.data = barData

// 设置柱状图外观和行为
        barChart.description.isEnabled = true
        barChart.xAxis.isEnabled = true
        barChart.axisLeft.isEnabled = true
        barChart.axisRight.isEnabled = true

        barChart.invalidate() // 刷新柱状图




        val refbutton=binding.resButton
        refbutton.setOnClickListener()
        {
            waveformChart.data = lineData
            waveformChart.invalidate()
        }


    }

    //读取json文件
    private fun loadJSONFromAsset(fileName: String): String {
        val inputStream: InputStream = assets.open(fileName)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charsets.UTF_8)
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
