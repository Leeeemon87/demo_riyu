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
import android.media.MediaPlayer
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import java.io.File
import java.io.FileInputStream

class PitchesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPitchesBinding
    private var isPlaying = false
    private lateinit var mediaPlayer: MediaPlayer
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

        val filePath = intent.getStringExtra("jsonPath")
        if (filePath.isNullOrEmpty()) {
            onBackPressed()
        }
        val jsonOri = loadJSONFromFile(filePath.toString())
        Log.v("jsonPath",filePath.toString())

        //val jsonOri=loadJSONFromAsset("result.json")


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

        while (countAll<listAllTime.length()-1){
            while (j<listStarts.length()){
                var left = listStarts.getDouble(j).toFloat()
                var right = listEnds.getDouble(j).toFloat()
                if(right>=listAllTime.getDouble(listAllTime.length()-1))
                {
                    right=listAllTime.getDouble(listAllTime.length()-1).toFloat()
                }
                while (listAllTime.getDouble(countAll).toFloat() < left){
                    entries.add(Entry(listAllTime.getDouble(countAll).toFloat(), 0F))
                    countAll++
                }
                while (listAllTime.getDouble(countAll).toFloat() < right){
                    Log.v("count",countAll.toString())
                    Log.v("right",right.toString())
                    Log.v("time",listAllTime.getDouble(countAll).toString())
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
            lineDataSet.lineWidth = 5f

            // 分配不同的颜色给每个线段
            val colorIndex = i % colors.size
            val lineColor = colors[colorIndex]
            lineDataSet.color = lineColor
            lineDataSet.setDrawFilled(true)
            lineDataSet.fillColor = lineColor
            lineDataSet.fillAlpha = 90
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
        meanChart.legend.isEnabled = true
        meanChart.xAxis.isEnabled = true
        meanChart.axisLeft.isEnabled = true
        meanChart.axisRight.isEnabled = true
        meanChart.xAxis.setDrawLabels(true)

        // 设置垂直虚线
        val xValues = mutableListOf<Float>()
        for (i in 0 until listStarts.length()) {
            xValues.add(listStarts.getDouble(i).toFloat())
            xValues.add(listEnds.getDouble(i).toFloat())
        }
        setupVerticalLines(meanChart.xAxis, xValues)

        val xLabels = mutableListOf<String>()
        val xStarts = mutableListOf<Float>()
        for (i in 0 until listStarts.length()) {
            xStarts.add(listStarts.getDouble(i).toFloat())
            xLabels.add(listHira.getString(i))
        }
        setupVerticalLinesWithLabels(meanChart.xAxis, xStarts,xLabels)
        // 刷新图表
        meanChart.invalidate()

        val audioPath = intent.getStringExtra("audioPath")

        // 按钮
        val refButton=binding.resButton
        mediaPlayer = MediaPlayer()

        // 按钮点击监听器
        refButton.setOnClickListener {
            if (!isPlaying) {
                mediaPlayer.setDataSource(audioPath)
                mediaPlayer.prepare()
                mediaPlayer.setOnCompletionListener {
                    isPlaying = false
                    refButton.text = "播放"
                }
                mediaPlayer.start()
                isPlaying = true
                refButton.text = "暂停"
            } else {
                mediaPlayer.stop()
                mediaPlayer.reset()
                isPlaying = false
                refButton.text = "播放"
            }
        }
    }


    private fun loadJSONFromAsset(fileName: String): String {
        val inputStream: InputStream = assets.open(fileName)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charsets.UTF_8)
    }
    private fun setupVerticalLinesWithLabels(xAxis: XAxis, xValues: List<Float>,xStarts: List<String>) {
        for (i in xValues.indices) {
            val limitLine = LimitLine(xValues[i])
            limitLine.lineColor = Color.LTGRAY
            limitLine.lineWidth = 1.5f
            limitLine.enableDashedLine(10f, 10f, 0f)
            limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
            limitLine.textSize = 12f
            limitLine.textColor=Color.DKGRAY

            // 设置 LimitLine 的 label，即 x 轴的值
            limitLine.label = xStarts[i]
            xAxis.addLimitLine(limitLine)
        }
    }

    private fun setupVerticalLines(xAxis: XAxis, xValues: List<Float>) {
        xAxis.removeAllLimitLines()

        for (xValue in xValues) {
            val limitLine = LimitLine(xValue)
            limitLine.lineColor = Color.LTGRAY
            limitLine.lineWidth = 1.5f
            limitLine.enableDashedLine(10f, 10f, 0f)
            limitLine.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
            limitLine.textSize = 8f
            limitLine.textColor=Color.GRAY

            // 设置 LimitLine 的 label，即 x 轴的值
            limitLine.label = xValue.toString()

            xAxis.addLimitLine(limitLine)
        }
    }

    //读取json文件
    private fun loadJSONFromFile(filePath: String): String {
        val file = File(filePath)
        if (!file.exists()) {
            return ""
        }
        val inputStream: InputStream = FileInputStream(file)
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
    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
}
