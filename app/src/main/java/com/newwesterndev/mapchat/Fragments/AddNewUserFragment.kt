package com.newwesterndev.mapchat.Fragments


import android.app.DialogFragment
import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.newwesterndev.mapchat.R

class AddNewUserFragment : DialogFragment() {

    private var mListener: AddNewUserDialogListener? = null

    interface AddNewUserDialogListener {
        fun onDialogPositiveClick(dialogFragment: DialogFragment, username: String)
        fun onDialogNegativeClick(dialogFragment: DialogFragment)
    }


}
