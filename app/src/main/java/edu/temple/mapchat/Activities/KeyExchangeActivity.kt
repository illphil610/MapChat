package edu.temple.mapchat.Activities

import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.newwesterndev.encrypt_keeper.Utilities.RSAEncryptUtility
import edu.temple.mapchat.R
import edu.temple.mapchat.Utilities.Utility
import kotlinx.android.synthetic.main.activity_key_exchange.*

class KeyExchangeActivity : AppCompatActivity(), NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    private lateinit var mNfcAdapter: NfcAdapter
    private lateinit var mUtility: Utility
    private lateinit var mEncryptDelegate: RSAEncryptUtility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_key_exchange)

        // Get my tool belt ready for work
        mUtility = Utility(this)
        mEncryptDelegate = RSAEncryptUtility()

        // Grab selected partners name so they dont have to input it
        val partnerName = intent.getStringExtra("partnerName")
        //Log.e("USER NAME FROM LIST", partnerName)
        partners_user_name.text = partnerName

        // NFC stuff and things
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (!mNfcAdapter.isEnabled) {
            mUtility.showToast(this, "Please enable NFC in settings")
        }
        mNfcAdapter.setNdefPushMessageCallback(this, this)
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this)
    }

    override fun createNdefMessage(event: NfcEvent?): NdefMessage {
        val prefs = this.getSharedPreferences("edu.temple.MapChat.USER_NAME", Context.MODE_PRIVATE)
        val currentUserName = prefs.getString("username", "hamsandwich")
        val currentPublicPEMFile = prefs.getString("username_public_pem", "empty")
        val recordsToAttach = mEncryptDelegate.createTransferNdefRecord(currentUserName, currentPublicPEMFile)
        return NdefMessage(recordsToAttach)
    }

    override fun onNdefPushComplete(event: NfcEvent?) {
        //Log.e("ONDEFPUSHCOMPLETE", "FUCK YEAHHH")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent?.action) {
            handleIntent(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            handleIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent?) {
        val rawMessages = intent?.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        val message = rawMessages?.get(0) as NdefMessage
        val mReceivedUserName = message.records[0].payload
        partners_user_name.text = String(mReceivedUserName)
        Log.e("Received User Name:", String(mReceivedUserName))

        val pemPublicKeyFile = String(message.records[1].payload)
        val formattedPublicKey = mEncryptDelegate.formatPemPublicKeyString(pemPublicKeyFile)
        partner_public_key.text = formattedPublicKey
        //Log.e("Received Public Key", formattedPublicKey)

        // Save given info to the PARTNER_LIST jawn in shared prefs (username, public key)
        // then go grab a beer and kick it with your home via MapShat

        //if (String(mReceivedUserName) == intent.getStringExtra("partnerName")) {
        val sharedPref = this.getSharedPreferences("edu.temple.mapchat.PARTNER_LIST" ,Context.MODE_PRIVATE) ?: return
        val editor = sharedPref.edit()
        editor.putString(String(mReceivedUserName), formattedPublicKey)
        editor.apply()
    }
}
