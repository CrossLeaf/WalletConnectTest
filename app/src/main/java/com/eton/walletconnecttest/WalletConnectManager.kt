package com.eton.walletconnecttest

import android.app.Activity
import android.util.Log
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet
import timber.log.Timber

object WalletConnectManager {
    val projectId =
        "042ce9d30ccb9664092eb63ec3c11b34" // Get Project ID at https://cloud.walletconnect.com/
    val relayUrl = "relay.walletconnect.com"
    val serverUrl = "wss://$relayUrl?projectId=$projectId"
    val connectionType = ConnectionType.AUTOMATIC
    val telemetryEnabled: Boolean = true
    val appMetaData = Core.Model.AppMetaData(
        name = "Wallet TB",
        description = "Wallet Description",
        url = "Wallet URL",
        icons = emptyList()/*list of icon url strings*/,
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
            Timber.tag(TAG).e("CoreClient initialize error %s", it.throwable.message)
        }

        val initParams = Wallet.Params.Init(core = CoreClient)
        Web3Wallet.initialize(initParams) { error ->
            // Error will be thrown if there's an issue during initialization
            Log.d("EEE", "error: $error")
        }
    }
}
