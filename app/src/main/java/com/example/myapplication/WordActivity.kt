package com.example.myapplication

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.File
import com.example.myapplication.databinding.ActivityWordBinding
import com.example.myapplication.ui.home.KanaItem
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.json.JSONObject
import java.io.InputStream


class WordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWordBinding
    private lateinit var seekBar: SeekBar
    private lateinit var startButton: Button
    private lateinit var verticalLine: View

    private val handler = Handler(Looper.getMainLooper())
    private val totalDurationMillis = 1000L // 总时间，单位：毫秒
    private val totalSteps = 100
    private var currentStep = 0
    private var speed = 1.0
    private var isMoving = false
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
            val jsonOri=loadJSONFromAsset("phones.json")
            val jsonObject = JSONObject(jsonOri)
            val smallKana=jsonObject.getJSONArray("small")
            val smallKanaList: List<String> = (0 until smallKana.length()).map { smallKana.getString(it) }

            // 创建 KanaItem 实例
            val kanaItem = KanaItem(smallKanaList,honmei, furikana, accent)

            val linearLayout: LinearLayout = binding.linear

            for (hiragana in kanaItem.kana) {
                val textView = TextView(this)
                textView.text = hiragana

                val layoutParams = LinearLayout.LayoutParams(
                    0, // 宽度设为 0dp
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.weight = 1.0f // 设置权重为 1，均分宽度
                textView.layoutParams = layoutParams
                textView.gravity = Gravity.CENTER
                textView.ellipsize = TextUtils.TruncateAt.END
                textView.textSize = 16f // 设置字体大小
                textView.setTextColor(Color.BLACK)
                linearLayout.addView(textView)
            }


            val meanChart: LineChart = binding.accentChart
            val lineDataSets = mutableListOf<ILineDataSet>() // 用于存储所有线的数据集

            for (i in 0 until kanaItem.n) {
                val hiraName = kanaItem.kana[i]
                val x1 = i*2F
                val y1 = kanaItem.acs[i].toFloat()
                val x2 = i*2+1.5F
                val y2 = kanaItem.acs[i].toFloat()

                val lineEntries = mutableListOf<Entry>()
                lineEntries.add(Entry(x1, y1))
                lineEntries.add(Entry(x2, y2))

                val lineDataSet = LineDataSet(lineEntries,kanaItem.kana[i].toString())
                lineDataSet.lineWidth = 5f

                val lineColor = Color.DKGRAY
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
            meanChart.setTouchEnabled(false)

            // 刷新图表
            meanChart.invalidate()
        }
        else{
            onBackPressed()
        }

        seekBar = binding.seekBar
        startButton = binding.startButton

        seekBar.max = totalSteps

        startButton.setOnClickListener {
            if (!isMoving) {
                isMoving = true
                currentStep = 0
                seekBar.progress = currentStep
                moveSeekBar()
                startButton.isEnabled = false
            }
        }

        val speedSpinner: Spinner = binding.speedSpinner
        val speedOptions = resources.getStringArray(R.array.speed_options)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, speedOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        speedSpinner.adapter = adapter

        speedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 根据选择的速度倍数来更新播放速度
                when (position) {
                    0 -> speed = 1.0
                    1 -> speed = 2.0
                    2 -> speed = 3.0
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        verticalLine = binding.verticalLine

        val layoutParams = verticalLine.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.marginStart = 0
        verticalLine.layoutParams = layoutParams


        // 设置 SeekBar 进度改变的监听器
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 计算竖向线段应该移动的距离，根据你的需求进行调整
                val lineX = progress.toFloat() * (binding.accentChart.width / totalSteps.toFloat()) // 每个进度单位的移动距离

                // 移动竖向线段
                val layoutParams = verticalLine.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.marginStart = lineX.toInt()
                verticalLine.layoutParams = layoutParams
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun moveSeekBar() {
        if (currentStep < totalSteps) {
            currentStep += 1
            seekBar.progress = currentStep

            // 计算每一步的时间间隔，考虑速度
            val stepDurationMillis:Float = totalDurationMillis.toFloat() / totalSteps
            val actualStepDurationMillis = (stepDurationMillis / speed).toLong()
            Log.v("mis",actualStepDurationMillis.toString())
            handler.postDelayed({ moveSeekBar() }, actualStepDurationMillis)
        } else {
            isMoving = false
            startButton.isEnabled = true
            val layoutParams = verticalLine.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.marginStart = 0
            verticalLine.layoutParams = layoutParams
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
