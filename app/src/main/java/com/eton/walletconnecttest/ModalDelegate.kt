package com.eton.walletconnecttest

import android.util.Log
import com.walletconnect.web3.modal.client.Modal
import com.walletconnect.web3.modal.client.Web3Modal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

object ModalDelegate : Web3Modal.ModalDelegate {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _wcEventModels: MutableSharedFlow<Modal.Model?> = MutableSharedFlow()
    val wcEventModels: SharedFlow<Modal.Model?> =  _wcEventModels.asSharedFlow()

    init {
        Web3Modal.setDelegate(this)
    }

    override fun onSessionApproved(approvedSession: Modal.Model.ApprovedSession) {
        Log.d("EEE", "onSessionApproved: $approvedSession")
        scope.launch {
            _wcEventModels.emit(approvedSession)
        }
    }

    override fun onSessionRejected(rejectedSession: Modal.Model.RejectedSession) {
        Log.d("EEE", "onSessionRejected: $rejectedSession")
        scope.launch {
            _wcEventModels.emit(rejectedSession)
        }
    }

    override fun onSessionUpdate(updatedSession: Modal.Model.UpdatedSession) {
        Log.d("EEE", "onSessionUpdate: $updatedSession")
        scope.launch {
            _wcEventModels.emit(updatedSession)
        }
    }

    override fun onSessionEvent(sessionEvent: Modal.Model.SessionEvent) {
        Log.d("EEE", "onSessionEvent: $sessionEvent")
        scope.launch {
            _wcEventModels.emit(sessionEvent)
        }
    }

    override fun onSessionEvent(sessionEvent: Modal.Model.Event) {
        Log.d("EEE", "onSessionEvent: $sessionEvent")
        scope.launch {
            _wcEventModels.emit(sessionEvent)
        }
    }

    override fun onSessionDelete(deletedSession: Modal.Model.DeletedSession) {
        Log.d("EEE", "onSessionDelete: $deletedSession")
        scope.launch {
            _wcEventModels.emit(deletedSession)
        }
    }

    override fun onSessionExtend(session: Modal.Model.Session) {
        Log.d("EEE", "onSessionExtend: $session")
        scope.launch {
            _wcEventModels.emit(session)
        }
    }

    override fun onSessionRequestResponse(response: Modal.Model.SessionRequestResponse) {
        Log.d("EEE", "onSessionRequestResponse: $response")
        scope.launch {
            _wcEventModels.emit(response)
        }
    }

    override fun onSessionAuthenticateResponse(sessionAuthenticateResponse: Modal.Model.SessionAuthenticateResponse) {
        Log.d("EEE", "onSessionAuthenticateResponse: $sessionAuthenticateResponse")
        scope.launch {
            _wcEventModels.emit(sessionAuthenticateResponse)
        }
    }

    override fun onSIWEAuthenticationResponse(response: Modal.Model.SIWEAuthenticateResponse) {
        Log.d("EEE", "SIWE response: $response")
        println("SIWE response: $response")
    }

    override fun onProposalExpired(proposal: Modal.Model.ExpiredProposal) {
        Log.d("EEE", "onProposalExpired: $proposal")
        scope.launch {
            _wcEventModels.emit(proposal)
        }
    }

    override fun onRequestExpired(request: Modal.Model.ExpiredRequest) {
        Log.d("EEE", "onRequestExpired: $request")
        scope.launch {
            _wcEventModels.emit(request)
        }
    }

    override fun onConnectionStateChange(state: Modal.Model.ConnectionState) {
        Log.d("EEE", "onConnectionStateChange: $state")
        scope.launch {
            _wcEventModels.emit(state)
        }
    }

    override fun onError(error: Modal.Model.Error) {
        Log.d("EEE", "onError: $error")
        Timber.d("EEE", error.throwable.stackTraceToString())
        scope.launch {
            _wcEventModels.emit(error)
        }
    }

}