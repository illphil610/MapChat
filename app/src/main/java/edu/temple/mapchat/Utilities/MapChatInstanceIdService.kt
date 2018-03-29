package edu.temple.mapchat.Utilities

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import edu.temple.mapchat.R
import retrofit2.Call
import retrofit2.Response

/**
 * Created by philip on 3/21/18.
 */
class MapChatInstanceIdService: FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.e(TAG, "Refreshed token: $refreshedToken")
        val mUtility = Utility(this)

        // Shared prefs cause im a Ganst-----A
        val preferences = getSharedPreferences("edu.temple.MapChat.USER_NAME", Context.MODE_PRIVATE)
        val user = preferences.getString("username", getString(R.string.defaultUser))

        if (user != getString(R.string.defaultUser)) {
            val requestInterface = mUtility.loadJSON()
            refreshedToken?.let {
                requestInterface.addUserToken(user, it)
                        .enqueue(object : retrofit2.Callback<Void> {
                            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                                Log.e("POST", "YAY $it")
                            }

                            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                                Log.e("POST", "NO")
                            }
                        })
            }
        } else {
            // Save FCM token to be sent when User creates their account.  Otherwise, this
            // method only runs when the app first runs on the phone.  So if the user hasnt already
            // created their account, and gets a FCM then Karls server would never know.
            val editor = preferences.edit()
            editor.putString("FCM_ID", refreshedToken)
            editor.apply()
            Log.e("SAVED FCM", refreshedToken)
        }
    }
}