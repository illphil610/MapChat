package edu.temple.mapchat.Activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.TextView
import co.intentservice.chatui.ChatView
import co.intentservice.chatui.models.ChatMessage
import com.newwesterndev.encrypt_keeper.Utilities.RSAEncryptUtility
import edu.temple.mapchat.Model.Model
import edu.temple.mapchat.Model.RxBus
import edu.temple.mapchat.R
import edu.temple.mapchat.Utilities.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_key_exchange.*
import org.jetbrains.anko.sdk25.coroutines.onClick

import retrofit2.Call
import retrofit2.Response
import java.security.KeyPair
import edu.temple.mapchat.Model.Message
import java.time.LocalTime


class ChatActivity : AppCompatActivity() {

    private lateinit var mDisposable: Disposable
    private var mCompositeDisposable = CompositeDisposable()
    private lateinit var mUtility: Utility
    private lateinit var rsaUtility: RSAEncryptUtility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mUtility = Utility(this)
        rsaUtility = RSAEncryptUtility()

        // Get partners stuff (name/public key)
        val partnerName = intent.getStringExtra("partner_name")
        //Set title of activity to partner name
        title = partnerName
        val partnerPublicKeyString = intent.getStringExtra("public_key")
        val partnerPublicKey = rsaUtility.getPublicKeyFromString(partnerPublicKeyString)
        Log.e("PARTNER PUBLIC", partnerPublicKey.toString())

        // Gets Users stuff
        val testPrivateKey = intent.getStringExtra("myPrivateJawn")
        val testPublicKey = intent.getStringExtra("myPublicJawn")
        Log.e("FUCK", testPublicKey)

        // Get users public/private key to encrypt messages from the strings from above
        val usersPrivateKey = rsaUtility.getPrivateKeyFromString(testPrivateKey)
        val usersPublicKey = rsaUtility.getPublicKeyFromString(testPublicKey)

        //  Get current username info
        val preferences = getSharedPreferences("edu.temple.MapChat.USER_NAME", Context.MODE_PRIVATE)
        val currentUser = preferences.getString("username", getString(R.string.defaultUser))

        // Handles the incoming message from Firebase (RxBus listens for ChatMessage)
        mCompositeDisposable.add(RxBus.listen(Model.ChatMessage::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("Message From FB", it.toString())
                    val sender = it.sentFromUser
                    val messageAsBytes = Base64.decode(it.message,  Base64.NO_WRAP)
                    val message = rsaUtility.decryptPublic(messageAsBytes, partnerPublicKey)

                    // update UI
                    chat_view.addMessage(ChatMessage(message, System.currentTimeMillis(), ChatMessage.Type.RECEIVED))
                    Log.e("DECRYPTED MSG", message)
                }))

        /*
        send_button.setOnClickListener {

            Log.e("TestLog", input_message_edit_text.text.toString())
            val referenceInterface = mUtility.loadJSON()
            val byteArray = rsaUtility.encryptPrivate(input_message_edit_text.text.toString(), usersPrivateKey)
            Log.e("DECRYPT", rsaUtility.decryptPublic(byteArray, usersPublicKey))

            // Convert byte array to a base 64 string before sending to FCM
            val bytesAsString = Base64.encodeToString(byteArray,  Base64.NO_WRAP)

            // Make our POST request to Karl's server with the info needed
            referenceInterface.sendMessage(currentUser, partnerName, bytesAsString)
                    .enqueue(object : retrofit2.Callback<Void> {
                        override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                            if (response?.isSuccessful!!) {
                                Log.e("MESSAGE",  response.body().toString())
                            } else {
                                Log.e("ERROR", response.errorBody().toString())
                            }
                        }
                        override fun onFailure(call: Call<Void>?, t: Throwable?) {
                            Log.e("MESSAGE", "Better luck next time")
                        }
                    })
        }
        */

        chat_view.setOnSentMessageListener(object : ChatView.OnSentMessageListener {
            override fun sendMessage(chatMessage: ChatMessage): Boolean {
                // perform actual message sending
                val referenceInterface = mUtility.loadJSON()
                val byteArray = rsaUtility.encryptPrivate(chatMessage.message.toString(), usersPrivateKey)

                // Convert byte array to a base 64 string before sending to FCM
                val bytesAsString = Base64.encodeToString(byteArray,  Base64.NO_WRAP)

                // Make our POST request to Karl's server with the info needed
                referenceInterface.sendMessage(currentUser, partnerName, bytesAsString)
                        .enqueue(object : retrofit2.Callback<Void> {
                            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                                if (response?.isSuccessful!!) {
                                    Log.e("MESSAGE",  response.body().toString())
                                } else {
                                    Log.e("ERROR", response.errorBody().toString())
                                }
                            }
                            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                                Log.e("MESSAGE", "Better luck next time")
                            }
                        })
                return true
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }
}
