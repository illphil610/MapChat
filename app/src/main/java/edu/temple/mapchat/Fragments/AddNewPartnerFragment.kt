package edu.temple.mapchat.Fragments

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.content.DialogInterface
import android.content.Intent
import edu.temple.mapchat.Activities.KeyExchangeActivity


/**
 * Created by philip on 3/24/18.
 */

class AddNewPartnerFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Sorry, you dont have the public key.  Want to add one?")
                .setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(activity, KeyExchangeActivity::class.java)
                    startActivity(intent)
                })
                .setNegativeButton("NO", DialogInterface.OnClickListener { dialog, which ->
                })
        return builder.create()
    }
}