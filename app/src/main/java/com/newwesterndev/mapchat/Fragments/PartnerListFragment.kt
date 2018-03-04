package com.newwesterndev.mapchat.Fragments

import android.content.Context
import android.os.Bundle
import android.app.Fragment
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
import kotlinx.android.synthetic.main.fragment_partner_list.*

class PartnerListFragment : Fragment(), DataAdapter.Listener {

    private var mPartnerList = ArrayList<Model.User>()
    private lateinit  var mDataAdapter: DataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        RxBus.listen(Model.UserList::class.java).subscribe({
            Log.e("RXBUS", it.users.toString())
            mPartnerList = it.users
            mDataAdapter.notifyDataSetChanged()

            //val user = Model.User("phil", "0", "0")
            //mPartnerList.add(user)
            Log.e("PARTNERLIST", mPartnerList.toString())
        })
        */
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
                    Log.e("RXBUS", it.users.toString())
                    mPartnerList.clear()
                    mPartnerList.addAll(it.users)
                    mDataAdapter.notifyDataSetChanged()
        })
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onItemClick(user: Model.User) {
        Toast.makeText(activity, "${user.username}, ${user.latitude}, ${user.longitude}", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(): PartnerListFragment {
            //val args = Bundle()
            //fragment.arguments = args
            return PartnerListFragment()
        }
    }
}
