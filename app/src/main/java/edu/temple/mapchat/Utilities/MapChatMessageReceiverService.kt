package edu.temple.mapchat.Utilities

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.temple.mapchat.Model.Model
import edu.temple.mapchat.Model.RxBus
import org.json.JSONObject

/**
 * Created by philip on 3/23/18.
 */
class MapChatMessageReceiverService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        // Get message from FCM
        val stringResponseFromKarl = remoteMessage?.data?.get("payload")

        // Convert to JSON object and parse out the data
        val json = JSONObject(stringResponseFromKarl)
        val sender = json.get("from").toString()
        val receiver = json.get("to").toString()
        val message = json.get("message").toString()
        Log.e(TAG, stringResponseFromKarl)

        //Create ChatMessage from response JSON and publish to the event bus
        val chatMessage = Model.ChatMessage(receiver, sender, message)
        RxBus.publish(chatMessage)
    }
}