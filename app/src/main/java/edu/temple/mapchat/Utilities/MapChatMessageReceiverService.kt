package edu.temple.mapchat.Utilities

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.temple.mapchat.Model.Model
import edu.temple.mapchat.Model.RxBus

/**
 * Created by philip on 3/23/18.
 */
class MapChatMessageReceiverService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        Log.e(TAG, remoteMessage?.data?.toString())
        Log.e(TAG, remoteMessage?.notification?.body)
        //val stringResponseFromKarl = remoteMessage?.data?.get("payload")

        //Create ChatMessage from response JSON
        val chatMessage = Model.ChatMessage("Dave", "Derek", "FUCK")
        // public results via RxBus
        RxBus.publish(chatMessage)
    }
}