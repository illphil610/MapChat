package com.newwesterndev.mapchat.Fragments

import android.content.Context
import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.newwesterndev.mapchat.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PartnerListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PartnerListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PartnerListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater?.inflate(R.layout.fragment_partner_list, container, false)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }


    companion object {
        fun newInstance(): PartnerListFragment {
            //val args = Bundle()
            //fragment.arguments = args
            return PartnerListFragment()
        }
    }
}
