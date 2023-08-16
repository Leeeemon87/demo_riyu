package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import okhttp3.*
import java.io.IOException
import android.provider.Settings
import android.widget.Toast
import org.json.JSONObject
import android.content.Context
import java.io.File
import android.net.Uri

class HomePageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sendRequestWithDeviceId()

        val cacheDir = cacheDir
        val avatarFile = File(cacheDir, "avatar.png")
        val avatarExists=avatarFile.exists()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(avatarExists) // 传递头像 URI
                }
            }
        }
    }

    fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun sendRequestWithDeviceId() {
        val deviceID = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val jsonObject = JSONObject()
        jsonObject.put("deviceId", deviceID)


        val url = "http://49.233.22.132:8080/demo/user/lastname?deviceId=$deviceID"

        val request = Request.Builder()
            .url(url)
            .build()

        val homeActivity = this@HomePageActivity

        runOnUiThread {
            Toast.makeText(homeActivity, "您的设备码" + deviceID, Toast.LENGTH_LONG).show()
        }

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

            }

            override fun onFailure(call: Call, e: IOException) {

            }
        })
        // 跳转至 MainActivity


    }

    @Composable
    fun LoginScreen(avatarExists:Boolean) {
        val context = LocalContext.current
        val cacheDir = cacheDir
        val avatarFile = File(cacheDir, "avatar.png")
        val avatarUri = if (avatarFile.exists()) {
            Uri.fromFile(avatarFile)
        } else {
            null
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 头像
            Image(
                painter = if (avatarExists) {
                    painterResource(id = R.drawable.seka) // 使用头像文件
                } else {
                    painterResource(id = R.drawable.seka) // 使用默认头像资源
                },
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )

            // 用户名输入框
            var username by remember { mutableStateOf("") }
            BasicTextField(
                value = username,
                onValueChange = { newUsername -> username = newUsername },
            )
            // 登录按钮
            Button(
                onClick = {

                    val requestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(
                            "deviceId",
                            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                        )
                        .addFormDataPart("timestamp", System.currentTimeMillis().toString())
                        .addFormDataPart(
                            "username",
                            resources.getString(R.string.zhuxu)
                        ) // 使用资源文件中的字符串
                        .build()
// 创建 OkHttp 客户端
                    val client = OkHttpClient()

// 构建 POST 请求
                    val url = "http://49.233.22.132:8080/demo/user/add" // 替换为实际的 API URL
                    val request = Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()


// 发送请求
                    client.newCall(request).enqueue(object : Callback {
                        override fun onResponse(call: Call, response: Response) {


                            val responseData = response.body()?.string()
                            if (responseData != null) {
                                // 使用 JSON 解析库解析服务器返回的 JSON 数据
                                try {
                                    val jsonObject = JSONObject(responseData)
                                    val code = jsonObject.getString("code")
                                    val info = jsonObject.getString("info")
                                    val token = jsonObject.getString("token")
                                    val homeActivity1 = this@HomePageActivity

                                    val sharedPreferences =
                                        getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString("token", token) // token 是您获取到的 token
                                    editor.apply()
                                    if (code == "0") {
                                        val intent = Intent(context, MainActivity::class.java)
                                        context.startActivity(intent)
                                        runOnUiThread {
                                            Toast.makeText(
                                                homeActivity1,
                                                "登陆成功！您的token是" + token,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } else {
                                        runOnUiThread {
                                            Toast.makeText(
                                                homeActivity1,
                                                "登陆失败！您的密码不对",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        override fun onFailure(call: Call, e: IOException) {
                            // 处理请求失败
                            val homeActivity2 = this@HomePageActivity
                            runOnUiThread {
                                Toast.makeText(homeActivity2, "登陆失败", Toast.LENGTH_LONG).show()
                            }
                        }
                    })

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Log in")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun LoginScreenPreview() {
        MyApplicationTheme {
            LoginScreen(false)
        }
    }
}
