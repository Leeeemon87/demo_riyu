package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.ui.AppBarConfiguration
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityMainBinding
import java.io.IOException
import okhttp3.Callback
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import org.json.JSONObject
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val recordPermissionCode = 101
    private lateinit var mediaRecorder: MediaRecorder
    private var isRecording = false

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recordButton = findViewById<ImageButton>(R.id.recordButton)
        val uploadButton = findViewById<Button>(R.id.uploadButton)

        recordButton.setOnClickListener {
            if (isRecording) GlobalScope.launch(Dispatchers.Main){
                recordButton.setImageResource(R.drawable.ic_record)
                delay(1000)
                stopRecording()

            } else {
                if (checkPermission()) {
                    startRecording()
                    recordButton.setImageResource(R.drawable.ic_stop)
                } else {
                    requestPermission()
                }
            }
        }
        uploadButton.setOnClickListener {
            uploadRecording() // 在uploadButton被点击时调用uploadRecording函数
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun checkPermission(): Boolean {
        val recordPermission = Manifest.permission.RECORD_AUDIO
        val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        return ContextCompat.checkSelfPermission(
            this,
            recordPermission
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    storagePermission
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            recordPermissionCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == recordPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start recording
                startRecording()
            } else {
                // Permission denied
                // Handle this as needed, such as showing an error message
            }
        }
    }

    private fun startRecording() {
        val outputFile = "${externalCacheDir?.absolutePath}/recording3.wav"
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setOutputFile(outputFile)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        uploadButton.visibility = View.INVISIBLE
        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
            isRecording = true
        } catch (e: IOException) {
            // Handle exception
        }
    }

    private fun stopRecording() {
        mediaRecorder.stop()
        mediaRecorder.release()
        isRecording = false
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        uploadButton.visibility = View.VISIBLE
    }


    private fun uploadRecording() {
        val uploadUrl = "http://49.233.22.132:8080/demo/upload"
        val outputFile = "${externalCacheDir?.absolutePath}/recording3.wav"

        val file = File(outputFile)

        val client = OkHttpClient()

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("username", "your_username") // 添加 username 参数
            .addFormDataPart("token", "your_token") // 添加 token 参数
            .addFormDataPart(
                "file",
                "recording3.wav",
                RequestBody.create(MediaType.parse("audio/wav"), file)
            )
            .build()

        val request: Request = Request.Builder()
            .url(uploadUrl)
            .post(requestBody)
            .build()



        val mainActivity = this@MainActivity
        val mainActivity2 = this@MainActivity

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(mainActivity, "fail", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    Toast.makeText(mainActivity2, "upload succeed", Toast.LENGTH_LONG).show()
                }
                val responseData = response.body()?.string() // 获取响应数据
                if (responseData != null) {
                    // 使用 JSON 解析库解析服务器返回的 JSON 数据
                    try {
                        val jsonObject = JSONObject(responseData)
                        val code = jsonObject.getString("code")
                        val info = jsonObject.getString("info")
                        val word = jsonObject.getString("data")
                        // 处理解析后的数据
                        runOnUiThread {
                            Toast.makeText(mainActivity2, "Status: $code, Message: $word", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }
}
