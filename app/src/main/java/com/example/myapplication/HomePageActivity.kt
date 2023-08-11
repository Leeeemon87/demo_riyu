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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.MainActivity
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import okhttp3.*
import java.io.IOException
import android.provider.Settings
import android.widget.Toast
import org.json.JSONObject
class HomePageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sendRequestWithDeviceId()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen() // 显示登录界面
                }
            }
        }
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
            Toast.makeText(homeActivity, "您的设备码"+deviceID, Toast.LENGTH_LONG).show()
        }

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                // 处理响应
            }

            override fun onFailure(call: Call, e: IOException) {
                // 处理失败
            }
        })
    // 跳转至 MainActivity
    fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}

@Composable
fun LoginScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 头像
        Image(
            painter = painterResource(id = R.drawable.sexy), // 本地图片资源
            contentDescription = null, // 图像描述
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

                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
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
        LoginScreen()
    }
}
}
