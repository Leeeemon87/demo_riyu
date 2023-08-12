package com.example.myapplication.ui.dashboard

import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import org.json.JSONObject
import java.io.FileInputStream
import java.io.FileOutputStream

class DashboardFragment : Fragment() {

    private val recordPermissionCode = 101
    private lateinit var mediaRecorder: MediaRecorder
    private var isRecording = false

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val recordButton:ImageButton = binding.recordButton
        val uploadButton = binding.uploadButton

        recordButton.setOnClickListener {
            if (isRecording) GlobalScope.launch(Dispatchers.Main){
                recordButton.setImageResource(R.drawable.ic_record)
                delay(400)
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
            uploadRecording()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun checkPermission(): Boolean {
        val recordPermission = Manifest.permission.RECORD_AUDIO
        val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        return ContextCompat.checkSelfPermission(
            requireContext(),
            recordPermission
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    storagePermission
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
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
        val outputFile = "${requireContext().externalCacheDir?.absolutePath}/recording3.wav"
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setOutputFile(outputFile)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        val uploadButton = binding.uploadButton
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
        val uploadButton = binding.uploadButton
        uploadButton.visibility = View.VISIBLE
    }

    fun copyAndRenameWavFile(sourceFilePath: String, destinationFilePath: String) {
        try {
            val sourceFile = File(sourceFilePath)
            if (!sourceFile.exists()) {
                println("Source file does not exist.")
                return
            }

            val destinationFile = File(destinationFilePath)
            if (destinationFile.exists()) {
                println("Destination file already exists.")
                return
            }

            FileInputStream(sourceFile).use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                }
            }

            println("File copied and renamed successfully.")
        } catch (e: IOException) {
            println("Error: ${e.message}")
        }
    }


    private fun uploadRecording() {
        val uploadUrl = "http://49.233.22.132:8080/demo/upload"
        val outputFile = "${requireContext().externalCacheDir?.absolutePath}/recording3.wav"

        val file = File(outputFile)

        val client = OkHttpClient()
        // 从 SharedPreferences 获取 token
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("username", "Zhuxu") // 添加 username 参数
            .addFormDataPart("token", token) // 添加 token 参数
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


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "fail", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "upload succeed", Toast.LENGTH_LONG).show()
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
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "Status: $code, Message: $word", Toast.LENGTH_LONG).show()
                        }
                        val timestamp = System.currentTimeMillis()
                        val folderName = "$timestamp" // 新文件夹的名称就是时间戳
                        val folderPath = "${requireContext().externalCacheDir?.absolutePath}/$folderName"

                        val folder = File(folderPath)
                        if (!folder.exists()) {
                            folder.mkdirs() // 创建新文件夹
                        }

                        val newFileName = "$word.wav"
                        val destinationFilePath = "$folderPath/$newFileName" // 新文件的完整路径

                        val sourceFilePath = "${requireContext().externalCacheDir?.absolutePath}/recording3.wav"

                        val sourceFile = File(sourceFilePath)
                        val destinationFile = File(destinationFilePath)

                        copyAndRenameWavFile(sourceFilePath, destinationFilePath)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }


}