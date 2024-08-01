package com.eton.walletconnecttest

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.walletconnect.web3.modal.client.Modal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

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

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        tvHello.setOnClickListener {
            navController.navigate(R.id.HomeFragment)

        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            ModalDelegate.wcEventModels.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect { event ->
                when (event) {
                    is Modal.Model.SessionRequestResponse -> {
                        when (event.result) {
                            is Modal.Model.JsonRpcResponse.JsonRpcError -> {
                                val error = event.result as Modal.Model.JsonRpcResponse.JsonRpcError
//                                navController.openAlertDialog("Error Message: ${error.message}\n Error Code: ${error.code}")
                                Log.d("EEE", "error: ${error.message}")
                            }

                            is Modal.Model.JsonRpcResponse.JsonRpcResult ->
                                Log.d(
                                    "EEE",
                                    "result: ${(event.result as Modal.Model.JsonRpcResponse.JsonRpcResult).result}"
                                )
//                                navController.openAlertDialog(
//                                (event.result as Modal.Model.JsonRpcResponse.JsonRpcResult).result
//                            )
                        }
                    }

                    is Modal.Model.Error -> {
                        Log.d("EEE", "error: ${event.throwable.message}")
//                        navController.openAlertDialog(
//                            event.throwable.localizedMessage ?: "Something went wrong"
//                        )
                    }

                    else -> Unit
                }
            }
        }

    }
}