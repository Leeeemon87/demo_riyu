package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityPitchesBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.json.JSONObject
import java.io.InputStream
import android.graphics.Color

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

        var countAll:Int=0
        var j:Int=0

        while (countAll<listAllTime.length()-100){
            while (j<listStarts.length()){
                val left = listStarts.getDouble(j).toFloat()
                val right = listEnds.getDouble(j).toFloat()
                while (listAllTime.getDouble(countAll).toFloat() < left){
                    entries.add(Entry(listAllTime.getDouble(countAll).toFloat(), 0F))
                    countAll++
                }
                while (listAllTime.getDouble(countAll).toFloat() <= right){
                    entries.add(Entry(listAllTime.getDouble(countAll).toFloat(), listAllValue.getDouble(countAll).toFloat()))
                    countAll++
                }
                j++
            }
            entries.add(Entry(listAllTime.getDouble(countAll).toFloat(), 0F))
            countAll++
        }

        val dataSet = LineDataSet(entries, "Label")
        val lineData = LineData(dataSet)

        val waveformChart: LineChart = findViewById(R.id.waveformChart)
        // Set the LineData to the chart
        waveformChart.data = lineData

        // Customize chart appearance and behavior as needed
        waveformChart.description.isEnabled = false
        waveformChart.legend.isEnabled = false

        waveformChart.axisLeft.isEnabled = true
        waveformChart.axisRight.isEnabled = true
        waveformChart.xAxis.isEnabled = true

        waveformChart.axisLeft.axisMinimum=0F
        waveformChart.axisLeft.axisMaximum=300F
        waveformChart.axisRight.axisMinimum=0F
        waveformChart.axisRight.axisMaximum=300F

        waveformChart.xAxis.axisMaximum=listEnds.getDouble(listEnds.length()-1).toFloat()+0.1F
        waveformChart.xAxis.axisMinimum=listEnds.getDouble(0).toFloat()-0.1F

        waveformChart.invalidate() // Refresh the chart

        //简化假名
        val meanChart: LineChart = binding.barChart
        val lineDataSets = mutableListOf<ILineDataSet>() // 用于存储所有线的数据集

        // 定义颜色数组，用于为每个线段分配不同的颜色
        val colors = arrayOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA,Color.CYAN)

        for (i in 0 until listHira.length()) {
            val hiraName = listHira.getString(i)
            val x1 = listStarts.getDouble(i).toFloat()
            val y1 = listMean.getDouble(i).toFloat()
            val x2 = listEnds.getDouble(i).toFloat()
            val y2 = listMean.getDouble(i).toFloat()

            val lineEntries = mutableListOf<Entry>()
            lineEntries.add(Entry(x1, y1))
            lineEntries.add(Entry(x2, y2))

            val lineDataSet = LineDataSet(lineEntries, hiraName)
            // 启用数据点的值显示，并设置其格式
            lineDataSet.setDrawValues(true)
//            lineDataSet.valueFormatter = ValueFormatter() {
//                override fun getFormattedValue(value: Float): String {
//                    return hiraName
//                }
//            }

            // 分配不同的颜色给每个线段
            val colorIndex = i % colors.size
            val lineColor = colors[colorIndex]
            lineDataSet.color = lineColor
            lineDataSet.setDrawFilled(true)
            lineDataSet.fillColor = lineColor
            lineDataSet.fillAlpha = 70

            lineDataSets.add(lineDataSet)
        }

        val meanData = LineData(lineDataSets)
        meanChart.data = meanData

        // 设置图表外观和行为
        meanChart.description.isEnabled = false
        meanChart.legend.isEnabled = true
        meanChart.xAxis.isEnabled = true
        meanChart.axisLeft.isEnabled = true
        meanChart.axisRight.isEnabled = true

        // 刷新图表
        meanChart.invalidate()



        // 按钮
        val refbutton=binding.resButton
        refbutton.setOnClickListener()
        {

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
