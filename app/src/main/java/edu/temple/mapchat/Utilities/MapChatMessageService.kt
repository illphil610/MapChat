package edu.temple.mapchat.Utilities

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import retrofit2.Call
import retrofit2.Response

/**
 * Created by philip on 3/21/18.
 */
class MapChatMessageService: FirebaseInstanceIdService() {

    private var hasTokenBeenSentToKarlsServer: Boolean = false
    private val mUtility = Utility(this)

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.e(TAG, "Refreshed token: $refreshedToken")

        // send id to karls server
        // this if statement is dumb
        /*
        if (!hasTokenBeenSentToKarlsServer) {
            val requestInterface = mUtility.loadJSON()
            refreshedToken?.let {
                requestInterface.addUserToken("Phil", it)
                    .enqueue(object : retrofit2.Callback<Void> {
                        override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                            Log.e("POST", "YAY $it")
                        }
                        override fun onFailure(call: Call<Void>?, t: Throwable?) {
                            Log.e("POST", "NO")
                        }
                    })
            }
        }
        */
    }
}