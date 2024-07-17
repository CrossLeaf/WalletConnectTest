package com.eton.walletconnecttest

import android.util.Log
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

object WCDelegate : Web3Wallet.WalletDelegate, CoreClient.CoreDelegate {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _coreEvents: MutableSharedFlow<Core.Model> = MutableSharedFlow()
    val coreEvents: SharedFlow<Core.Model> = _coreEvents.asSharedFlow()

    private val _walletEvents: MutableSharedFlow<Wallet.Model> = MutableSharedFlow()
    val walletEvents: SharedFlow<Wallet.Model> = _walletEvents.asSharedFlow()
    private val _connectionState: MutableSharedFlow<Wallet.Model.ConnectionState> =
        MutableSharedFlow(replay = 1)
    val connectionState: SharedFlow<Wallet.Model.ConnectionState> = _connectionState.asSharedFlow()
    var authRequestEvent: Pair<Wallet.Model.AuthRequest, Wallet.Model.VerifyContext>? = null
    var sessionProposalEvent: Pair<Wallet.Model.SessionProposal, Wallet.Model.VerifyContext>? = null
    var sessionAuthenticateEvent: Pair<Wallet.Model.SessionAuthenticate, Wallet.Model.VerifyContext>? =
        null
    var sessionRequestEvent: Pair<Wallet.Model.SessionRequest, Wallet.Model.VerifyContext>? = null
    var currentId: Long? = null

    init {
        CoreClient.setDelegate(this)
        Web3Wallet.setWalletDelegate(this)
    }

    override fun onAuthRequest(
        authRequest: Wallet.Model.AuthRequest,
        verifyContext: Wallet.Model.VerifyContext
    ) {
        authRequestEvent = Pair(authRequest, verifyContext)

        scope.launch {
            _walletEvents.emit(authRequest)
        }
    }

    override fun onConnectionStateChange(state: Wallet.Model.ConnectionState) {
        scope.launch {
            _connectionState.emit(state)
        }
    }

    override fun onError(error: Wallet.Model.Error) {
        Log.d("EEE", "error "+JSONObject().put("error", error.throwable.stackTraceToString()))
        scope.launch {
            _walletEvents.emit(error)
        }
    }

    override fun onSessionDelete(sessionDelete: Wallet.Model.SessionDelete) {
        scope.launch {
            _walletEvents.emit(sessionDelete)
        }
    }

    override fun onSessionExtend(session: Wallet.Model.Session) {
        Log.d("Session Extend", "${session.expiry}")
    }

    override fun onSessionProposal(
        sessionProposal: Wallet.Model.SessionProposal,
        verifyContext: Wallet.Model.VerifyContext
    ) {
        sessionProposalEvent = Pair(sessionProposal, verifyContext)

        scope.launch {
            _walletEvents.emit(sessionProposal)
        }
    }

    override val onSessionAuthenticate: (Wallet.Model.SessionAuthenticate, Wallet.Model.VerifyContext) -> Unit
        get() = { sessionAuthenticate, verifyContext ->

            sessionAuthenticateEvent = Pair(sessionAuthenticate, verifyContext)

            scope.launch {
                _walletEvents.emit(sessionAuthenticate)
            }
        }

    override fun onSessionRequest(
        sessionRequest: Wallet.Model.SessionRequest,
        verifyContext: Wallet.Model.VerifyContext
    ) {
        if (currentId != sessionRequest.request.id) {
            sessionRequestEvent = Pair(sessionRequest, verifyContext)

            scope.launch {
                _walletEvents.emit(sessionRequest)
            }
        }
    }

    override fun onSessionSettleResponse(settleSessionResponse: Wallet.Model.SettledSessionResponse) {
        scope.launch {
            _walletEvents.emit(settleSessionResponse)
        }
    }

    override fun onSessionUpdateResponse(sessionUpdateResponse: Wallet.Model.SessionUpdateResponse) {
        scope.launch {
            _walletEvents.emit(sessionUpdateResponse)
        }
    }

    override fun onProposalExpired(proposal: Wallet.Model.ExpiredProposal) {
        scope.launch {
            _walletEvents.emit(proposal)
        }
    }

    override fun onRequestExpired(request: Wallet.Model.ExpiredRequest) {
        scope.launch {
            _walletEvents.emit(request)
        }
    }

    override fun onPairingDelete(deletedPairing: Core.Model.DeletedPairing) {
        scope.launch {
            _coreEvents.emit(deletedPairing)
        }
    }

    override fun onPairingExpired(expiredPairing: Core.Model.ExpiredPairing) {
        scope.launch {
            _coreEvents.emit(expiredPairing)
        }
    }

    override fun onPairingState(pairingState: Core.Model.PairingState) {
        scope.launch {
            _coreEvents.emit(pairingState)
        }
    }

    val walletDelegate = object : SignClient.WalletDelegate {

        override fun onSessionExtend(session: Sign.Model.Session) {
            Log.d("EEE", "WalletDelegate onSessionExtend:  $session")
        }

        override fun onSessionProposal(
            sessionProposal: Sign.Model.SessionProposal,
            verifyContext: Sign.Model.VerifyContext
        ) {
            Log.d("EEE", "WalletDelegate onSessionProposal:  $sessionProposal")
        }

        override fun onSessionRequest(
            sessionRequest: Sign.Model.SessionRequest,
            verifyContext: Sign.Model.VerifyContext
        ) {
            Log.d("EEE", "WalletDelegate onSessionRequest:  $sessionRequest")
        }

        override fun onSessionSettleResponse(settleSessionResponse: Sign.Model.SettledSessionResponse) {
            Log.d("EEE", "WalletDelegate onSessionSettleResponse:  $settleSessionResponse")
        }

        override fun onSessionUpdateResponse(sessionUpdateResponse: Sign.Model.SessionUpdateResponse) {
            Log.d("EEE", "WalletDelegate onSessionUpdateResponse:  $sessionUpdateResponse")
        }

        override fun onConnectionStateChange(state: Sign.Model.ConnectionState) {
            Log.d("EEE", "WalletDelegate onConnectionStateChange:  $state")
        }

        override fun onError(error: Sign.Model.Error) {
            Log.d("EEE", "WalletDelegate onError:  $error")
        }

        override fun onSessionDelete(deletedSession: Sign.Model.DeletedSession) {
            Log.d("EEE", "WalletDelegate onSessionDelete:  $deletedSession")
        }
    }

    val dappDelegate = object : SignClient.DappDelegate {
        override fun onConnectionStateChange(state: Sign.Model.ConnectionState) {
            Log.d("EEE", "DappDelegate onConnectionStateChange:  $state")
        }

        override fun onError(error: Sign.Model.Error) {
            Log.d("EEE", "DappDelegate onError:  $error")
        }

        override fun onProposalExpired(proposal: Sign.Model.ExpiredProposal) {
            Log.d("EEE", "DappDelegate onProposalExpired:  $proposal")
        }

        override fun onRequestExpired(request: Sign.Model.ExpiredRequest) {
            Log.d("EEE", "DappDelegate onRequestExpired:  $request")
        }

        override fun onSessionApproved(approvedSession: Sign.Model.ApprovedSession) {
            Log.d("EEE", "DappDelegate onSessionApproved:  $approvedSession")
        }

        override fun onSessionDelete(deletedSession: Sign.Model.DeletedSession) {
            Log.d("EEE", "DappDelegate onSessionDelete:  $deletedSession")
        }

        override fun onSessionEvent(sessionEvent: Sign.Model.SessionEvent) {
            Log.d("EEE", "DappDelegate onSessionEvent:  $sessionEvent")
        }

        override fun onSessionExtend(session: Sign.Model.Session) {
            Log.d("EEE", "DappDelegate onSessionExtend:  $session")
        }

        override fun onSessionRejected(rejectedSession: Sign.Model.RejectedSession) {
            Log.d("EEE", "DappDelegate onSessionRejected:  $rejectedSession")
        }

        override fun onSessionRequestResponse(response: Sign.Model.SessionRequestResponse) {
            Log.d("EEE", "DappDelegate onSessionRequestResponse:  $response")
        }

        override fun onSessionUpdate(updatedSession: Sign.Model.UpdatedSession) {
            Log.d("EEE", "DappDelegate onSessionUpdate:  $updatedSession")
        }

        override fun onSessionAuthenticateResponse(sessionAuthenticateResponse: Sign.Model.SessionAuthenticateResponse) {
            super.onSessionAuthenticateResponse(sessionAuthenticateResponse)
            Log.d("EEE", "DappDelegate onSessionAuthenticateResponse:  $sessionAuthenticateResponse")
        }

        override fun onSessionEvent(sessionEvent: Sign.Model.Event) {
            super.onSessionEvent(sessionEvent)
            Log.d("EEE", "DappDelegate onSessionEvent:  $sessionEvent")
        }
    }
}