package edu.temple.mapchat.Fragments

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.content.DialogInterface
import android.content.Intent
import android.graphics.ColorSpace
import android.util.Log
import edu.temple.mapchat.Activities.KeyExchangeActivity


/**
 * Created by philip on 3/24/18.
 */

class AddNewPartnerFragment : DialogFragment() {

    private var mListener: AddNewPartnerInterface? = null

    interface AddNewPartnerInterface {
        fun onPartnerDialogPositiveClick(dialogFragment: DialogFragment)
        fun onPartnerDialogNegativeClick(dialogFragment: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Sorry, you dont have the public key.  Want to add one?")
                .setPositiveButton("YES", { _ , _ ->
                    mListener?.onPartnerDialogPositiveClick(this)
                })
                .setNegativeButton("NO", { _ , _ ->
                    mListener?.onPartnerDialogNegativeClick(this)
                })
        return builder.create()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as (AddNewPartnerFragment.AddNewPartnerInterface)
    }
}