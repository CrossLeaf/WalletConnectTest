package com.eton.walletconnecttest

import android.app.Application
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.util.bytesToHex
import com.walletconnect.util.randomBytes
import com.walletconnect.web3.modal.client.Modal
import com.walletconnect.web3.modal.client.Web3Modal
import com.walletconnect.web3.modal.presets.Web3ModalChainsPresets
import com.walletconnect.web3.modal.utils.EthUtils
import timber.log.Timber

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val appMetaData = Core.Model.AppMetaData(
            name = "Kotlin Modals",
            description = "Kotlin Modals Lab Sample",
            url = "https://web3modal-laboratory-git-chore-kotlin-assetlinks-walletconnect1.vercel.app",
            icons = listOf("https://gblobscdn.gitbook.com/spaces%2F-LJJeCjcLrr53DcT1Ml7%2Favatar.png?alt=media"),
            redirect = "kotlin-modal-wc://request",
            linkMode = true,
            appLink = "https://web3modal-laboratory-git-chore-kotlin-assetlinks-walletconnect1.vercel.app/lab"
        )

        CoreClient.initialize(
            projectId = "042ce9d30ccb9664092eb63ec3c11b34",
            connectionType = ConnectionType.AUTOMATIC,
            application = this,
            metaData = appMetaData,
        ) {
            Timber.e(it.throwable)
        }

        Web3Modal.initialize(Modal.Params.Init(core = CoreClient)) { error ->
            Timber.e("EEE", error.throwable.stackTraceToString())
        }

        Web3Modal.setChains(Web3ModalChainsPresets.ethChains.values.toList())

        val authParams = Modal.Model.AuthPayloadParams(
            chains = Web3ModalChainsPresets.ethChains.values.toList().map { it.id },
            domain = "sample.kotlin.modal",
            uri = "https://web3inbox.com/all-apps",
            nonce = randomBytes(12).bytesToHex(),
            statement = "I accept the Terms of Service: https://yourDappDomain.com/",
            methods = EthUtils.ethMethods
        )
        Web3Modal.setAuthRequestParams(authParams)

    }
}