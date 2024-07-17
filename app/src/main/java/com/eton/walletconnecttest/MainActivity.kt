package com.eton.walletconnecttest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tvHello = findViewById<TextView>(R.id.tvHello)

        WalletConnectManager.init(activity = this)
        WalletConnectManager.signInit(this)
        // Set an OnClickListener to the TextView
        tvHello.setOnClickListener {
            // Define what happens when the TextView is clicked
            WalletConnectManager.signConnect(this)
//            startActivity(Intent(Intent.ACTION_VIEW, "tbpay://wc?uri=wc".toUri(), this, MainActivity::class.java))

        }
        handleIntent(intent)

    }

    override fun onResume() {
        super.onResume()
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            // 解析URI并处理
            val message = uri.getQueryParameter("message")
            val signature = uri.getQueryParameter("signature")
            val approved = uri.getQueryParameter("approved")

            if (message != null && signature != null && approved != null) {
                if (approved == "true") {
                    // 签名成功，处理签名
                    verifySignature(message, signature)
                } else {
                    // 签名失败，处理错误
                    println("Signature not approved")
                }
            }
        }
    }

    private fun verifySignature(message: String, signature: String) {
        // 实现签名验证逻辑
        println("Message: $message")
        println("Signature: $signature")
        // 这里你可以将message和signature发送到你的服务器进行验证
    }
}