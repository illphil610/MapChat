package com.newwesterndev.mapchat.Fragments


import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.newwesterndev.mapchat.R
import kotlinx.android.synthetic.main.fragment_add_new_user.view.*

class AddNewUserFragment : DialogFragment() {

    private var mListener: AddNewUserDialogListener? = null

    interface AddNewUserDialogListener {
        fun onDialogPositiveClick(dialogFragment: DialogFragment, username: String)
        fun onDialogNegativeClick(dialogFragment: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val layout = inflater.inflate(R.layout.fragment_add_new_user, null)

        builder.setView(layout)
                .setPositiveButton("Create", { _, _ ->
                    mListener?.onDialogPositiveClick(this, layout.user_name_edit.text.toString())
                })
                .setNegativeButton("Cancel", { _, _ ->
                    mListener?.onDialogNegativeClick(this)
                })

        return builder.create()

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as (AddNewUserDialogListener)
    }


}
