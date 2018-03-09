package com.newwesterndev.mapchat.Fragments

import android.content.Context
import android.os.Bundle
import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.support.design.widget.FloatingActionButton
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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_partner_list.*

class PartnerListFragment : Fragment(), DataAdapter.Listener {

    private var mPartnerList = ArrayList<Model.User>()
    private lateinit  var mDataAdapter: DataAdapter
    private lateinit var mPartnerListInterface: PartnerListInterface
    private var mCompositeDisposable = CompositeDisposable()
    private lateinit var mDisposable: Disposable
    //private lateinit var fab:

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_partner_list, container, false)
        val partnerList = view?.findViewById(R.id.partnerList) as RecyclerView
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(activity)
        partnerList.layoutManager = layoutManager
        partnerList.setHasFixedSize(true)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            Log.e("FAB", "pressed")
        }

        mDataAdapter = DataAdapter(mPartnerList, this)
        partnerList.adapter = mDataAdapter
        return view
    }

    override fun onItemClick(user: Model.User) {
        Toast.makeText(activity, "${user.username}, ${user.latitude}, " +
                "${user.longitude}", Toast.LENGTH_SHORT).show()
        mPartnerListInterface.userItemSelected()
    }

    private inline fun FragmentManager.inTransaction(
            func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().addToBackStack(null).commit()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mPartnerListInterface = context as PartnerListInterface
    }

    override fun onResume() {
        super.onResume()

        mDisposable = RxBus.listen(Model.UserList::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    mPartnerList.clear()
                    mPartnerList.addAll(it.users)
                    Log.e("Partners", "partners updated")
                    mDataAdapter.notifyDataSetChanged()
                })
    }

    override fun onStop() {
        super.onStop()
        mCompositeDisposable.add(mDisposable)
        mCompositeDisposable.clear()
    }

    companion object {
        fun newInstance(): PartnerListFragment {
            return PartnerListFragment()
        }
    }

    interface PartnerListInterface {
        fun userItemSelected()
    }
}
