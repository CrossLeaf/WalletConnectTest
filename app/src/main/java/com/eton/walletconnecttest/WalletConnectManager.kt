package com.eton.walletconnecttest

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet

object WalletConnectManager {
    private const val TAG = "EEE"
    val projectId =
        "042ce9d30ccb9664092eb63ec3c11b34" // Get Project ID at https://cloud.walletconnect.com/
    val relayUrl = "relay.walletconnect.com"
    val serverUrl = "wss://$relayUrl?projectId=$projectId"
    val connectionType = ConnectionType.AUTOMATIC
    val telemetryEnabled: Boolean = true
    val appMetaData = Core.Model.AppMetaData(
        name = "Wallet Name",
        description = "Wallet Description",
        url = "Wallet URL",
        icons = listOf("https://raw.githubusercontent.com/WalletConnect/walletconnect-assets/master/Icon/Gradient/Icon.png")/*list of icon url strings*/,
        redirect = "kotlin-wallet-wc:/request" // Custom Redirect URI
    )


    fun init(activity: Activity) {
        CoreClient.initialize(
            relayServerUrl = serverUrl,
            connectionType = connectionType,
            application = activity.application,
            metaData = appMetaData,
            telemetryEnabled = telemetryEnabled
        ) { error: Core.Model.Error ->
            Log.d(TAG, "CoreClient initialize error  ${error.throwable.message}")
        }

        val initParams = Wallet.Params.Init(core = CoreClient)

        Web3Wallet.initialize(initParams,
            onSuccess = {
                Log.d(TAG, "Web3Wallet initialize onSuccess")
            }, onError = {
                Log.d(TAG, "Web3Wallet initialize error  ${it.throwable.message}")
            })
    }

    fun signInit(activity: Activity) {
        CoreClient.initialize(
            relayServerUrl = serverUrl,
            connectionType = connectionType,
            application = activity.application,
            metaData = appMetaData,
            telemetryEnabled = telemetryEnabled
        ) { error: Core.Model.Error ->
            Log.d(TAG, "CoreClient initialize error  ${error.throwable.message}")
        }
        SignClient.initialize(Sign.Params.Init(core = CoreClient),
            onSuccess = {
                Log.d(TAG, "SignClient initialize onSuccess")

            }, onError = {
                Log.d(TAG, "SignClient initialize error  ${it.throwable.message}")
            })
        SignClient.setDappDelegate(WCDelegate.dappDelegate)
        SignClient.setWalletDelegate(WCDelegate.walletDelegate)

    }

    fun signConnect(activity: Activity) {
        // 設置以太坊的命名空間和參數
        val namespace = "eip155"
        val chains = listOf("eip155:1") // 1 表示以太坊主網
        val methods = listOf("eth_sendTransaction", "personal_sign", "eth_signTypedData") // 所需的方法
        val events = listOf("accountsChanged", "chainChanged") // 所需的事件
        val accounts = listOf<String>() // 可選：在這裡添加具體帳戶，如果已知
        val namespaces = mapOf(
            namespace to Sign.Model.Namespace.Proposal(chains, methods, events)
        )
//        val pairing = Core.Model.Pairing("", 0L, null, "", null, "", true, "") // 初始化或獲取現有配對
        CoreClient.Pairing.create()?.let {
            val connectParams = Sign.Params.Connect(namespaces, pairing = it)

            SignClient.connect(connectParams,
                { onSuccess ->
                    /*callback that returns letting you know that you have successfully initiated connecting*/
                    Log.d(TAG, "SignClient connect onSuccess: $onSuccess")
                    CoreClient.Pairing.pair(Core.Params.Pair(onSuccess), {
                        Log.d(TAG, "SignClient pair onSuccess: $it")
                        if (it.uri.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = it.uri.toUri()
                            startActivity(activity, intent, null)
                        } else {
                            println("Failed to generate connect URI")
                        }
                    }, {
                        Log.d(TAG, "SignClient pair error: ${it.throwable}")
                    })

                },
                { error ->
                    /*callback for error while trying to initiate a connection with a peer*/
                    Log.d(TAG, "SignClient connect error: ${error.throwable}")
                }
            )
        }
    }
}
