package com.newwesterndev.mapchat.Fragments

import android.content.Context
import android.os.Bundle
import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.newwesterndev.mapchat.Adapter.DataAdapter
import com.newwesterndev.mapchat.Model.Model
import com.newwesterndev.mapchat.Model.RxBus

import com.newwesterndev.mapchat.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PartnerListFragment : Fragment(), DataAdapter.Listener {

    private var mPartnerList = ArrayList<Model.User>()
    private lateinit  var mDataAdapter: DataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_partner_list, container, false)
        val partnerList = view?.findViewById(R.id.partnerList) as RecyclerView
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(activity)
        partnerList.layoutManager = layoutManager
        partnerList.setHasFixedSize(true)

        mDataAdapter = DataAdapter(mPartnerList, this)
        partnerList.adapter = mDataAdapter

        RxBus.listen(Model.UserList::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    mPartnerList.clear()
                    mPartnerList.addAll(it.users)
                    mDataAdapter.notifyDataSetChanged()
        })
        return view
    }

    override fun onItemClick(user: Model.User) {
        Toast.makeText(activity, "${user.username}, ${user.latitude}, " +
                "${user.longitude}", Toast.LENGTH_SHORT).show()

        fragmentManager.inTransaction {
            replace(R.id.mapchat_nav_fragment, MapFragment.newInstance())
        }
    }

    private inline fun FragmentManager.inTransaction(
            func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().addToBackStack(null).commit()
    }

    companion object {
        fun newInstance(): PartnerListFragment {
            //val args = Bundle()
            //fragment.arguments = args
            return PartnerListFragment()
        }
    }
}
