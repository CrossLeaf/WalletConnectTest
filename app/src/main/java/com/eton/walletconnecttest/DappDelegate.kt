package com.eton.walletconnecttest

import com.walletconnect.android.CoreClient
import com.walletconnect.sign.BuildConfig
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

open class DappDelegate : SignClient.DappDelegate {
    override fun onSessionRejected(rejectedSession: Sign.Model.RejectedSession) {}
    override fun onSessionUpdate(updatedSession: Sign.Model.UpdatedSession) {}
    override fun onSessionEvent(sessionEvent: Sign.Model.SessionEvent) {}
    override fun onSessionEvent(sessionEvent: Sign.Model.Event) {}
    override fun onSessionExtend(session: Sign.Model.Session) {}
    override fun onSessionDelete(deletedSession: Sign.Model.DeletedSession) {}
    override fun onSessionRequestResponse(response: Sign.Model.SessionRequestResponse) {}
    override fun onProposalExpired(proposal: Sign.Model.ExpiredProposal) {}
    override fun onRequestExpired(request: Sign.Model.ExpiredRequest) {}
    override fun onSessionAuthenticateResponse(sessionAuthenticateResponse: Sign.Model.SessionAuthenticateResponse) {}
    override fun onSessionApproved(approvedSession: Sign.Model.ApprovedSession) {}
    override fun onConnectionStateChange(state: Sign.Model.ConnectionState) {
        Timber.d("Dapp: onConnectionStateChange: $state")
    }

    override fun onError(error: Sign.Model.Error) {
    }
}

open class AutoApproveDappDelegate(val onSessionApprovedSuccess: (approvedSession: Sign.Model.ApprovedSession) -> Unit) : DappDelegate() {
    override fun onSessionApproved(approvedSession: Sign.Model.ApprovedSession) {
        approvedSession.onSessionApproved { onSessionApprovedSuccess(approvedSession) }
    }
}

fun Sign.Model.ApprovedSession.onSessionApproved(onSuccess: () -> Unit) {
    Timber.d("dappDelegate: onSessionApproved")
}