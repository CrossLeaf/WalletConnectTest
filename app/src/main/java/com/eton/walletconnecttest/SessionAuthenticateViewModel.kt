package com.eton.walletconnecttest

import androidx.lifecycle.ViewModel
import com.walletconnect.android.cacao.signature.SignatureType
import com.walletconnect.android.internal.common.exception.NoConnectivityException
import com.walletconnect.android.utils.cacao.sign
import com.walletconnect.util.hexToBytes
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet
import com.walletconnect.web3.wallet.utils.CacaoSigner

class SessionAuthenticateViewModel : ViewModel() {

    fun approve(onSuccess: (String) -> Unit = {}, onError: (Throwable) -> Unit = {}) {
        if (WCDelegate.sessionAuthenticateEvent != null) {
            try {
                val sessionAuthenticate = WCDelegate.sessionAuthenticateEvent!!.first
                val auths = mutableListOf<Wallet.Model.Cacao>()

                val authPayloadParams =
                    Web3Wallet.generateAuthPayloadParams(
                        sessionAuthenticate.payloadParams,
                        supportedChains = listOf("eip155:1", "eip155:137", "eip155:56"),
                        supportedMethods = listOf("personal_sign", "eth_signTypedData", "eth_signTypedData_v4", "eth_sign")
                    )

//                authPayloadParams.chains
//                    .forEach { chain ->
//                        val issuer = "did:pkh:$chain:$ACCOUNTS_1_EIP155_ADDRESS"
//                        val message = Web3Wallet.formatAuthMessage(Wallet.Params.FormatAuthMessage(authPayloadParams, issuer))
//                        val signature = CacaoSigner.sign(message, EthAccountDelegate.privateKey.hexToBytes(), SignatureType.EIP191)
//                        val auth = Web3Wallet.generateAuthObject(authPayloadParams, issuer, signature)
//                        auths.add(auth)
//                    }

                val approveProposal = Wallet.Params.ApproveSessionAuthenticate(id = sessionAuthenticate.id, auths = auths)
                Web3Wallet.approveSessionAuthenticate(approveProposal,
                    onSuccess = {
                        WCDelegate.sessionAuthenticateEvent = null
                        onSuccess(sessionAuthenticate.participant.metadata?.redirect ?: "")
                    },
                    onError = { error ->
                        if (error.throwable !is NoConnectivityException) {
                            WCDelegate.sessionAuthenticateEvent = null
                        }
                        onError(error.throwable)
                    }
                )
            } catch (e: Exception) {
                WCDelegate.sessionAuthenticateEvent = null
                onError(e)
            }
        } else {
            onError(Throwable("Authenticate request expired"))
        }
    }

    fun reject(onSuccess: (String) -> Unit = {}, onError: (Throwable) -> Unit = {}) {
        if (WCDelegate.sessionAuthenticateEvent != null) {
            try {
                val sessionAuthenticate = WCDelegate.sessionAuthenticateEvent!!.first
                val rejectionReason = "Reject Session Authenticate"
                val reject = Wallet.Params.RejectSessionAuthenticate(
                    id = sessionAuthenticate.id,
                    reason = rejectionReason
                )

                Web3Wallet.rejectSessionAuthenticate(reject,
                    onSuccess = {
                        WCDelegate.sessionAuthenticateEvent = null
                        onSuccess(sessionAuthenticate.participant.metadata?.redirect ?: "")
                    },
                    onError = { error ->
                        if (error.throwable !is NoConnectivityException) {
                            WCDelegate.sessionAuthenticateEvent = null
                        }
                        onError(error.throwable)
                    })
            } catch (e: Exception) {
                WCDelegate.sessionAuthenticateEvent = null
                onError(e)
            }
        } else {
            onError(Throwable("Authenticate request expired"))
        }
    }
}