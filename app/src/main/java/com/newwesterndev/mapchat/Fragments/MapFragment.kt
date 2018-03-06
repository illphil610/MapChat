package com.newwesterndev.mapchat.Fragments

import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.newwesterndev.mapchat.R

class MapFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_map, container, false)
    }

    companion object {
        fun newInstance(): MapFragment {
            //val args = Bundle()
            //fragment.arguments = args
            return MapFragment()
        }
    }
}

