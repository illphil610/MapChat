package edu.temple.mapchat.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import edu.temple.mapchat.R
import kotlinx.android.synthetic.main.activity_key_exchange.*

class KeyExchangeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_key_exchange)

        // Grab selected partners name so they dont have to input it
        val partnerName = intent.getStringExtra("partnerName")
        Log.e("USER NAME FROM LIST", partnerName)
        partners_user_name.text = partnerName

        
    }
}
