package com.newwesterndev.mapchat.Utilities

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by philip on 3/21/18.
 */
class MapChatMessageService: FirebaseInstanceIdService() {

    val mUtility = Utility(applicationContext)
    var hasTokenBeenSentToKarlsServer: Boolean = false

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.e(TAG, "Refreshed token: $refreshedToken")

        // send id to karls server
        if (!hasTokenBeenSentToKarlsServer) {
            val requestInterface = mUtility.loadJSON()
            refreshedToken?.let { requestInterface.addUserToken("Phil", it) }
        }
    }
}