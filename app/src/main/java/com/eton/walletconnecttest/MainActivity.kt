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
//            startActivity(Intent(Intent.ACTION_VIEW, "tbpay://request".toUri(), this, MainActivity::class.java))
            WalletConnectManager.signConnect()
        }
    }
}