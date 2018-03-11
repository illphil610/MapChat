package com.newwesterndev.mapchat.Fragments

import android.Manifest
import android.app.*
import android.content.Context
import android.os.Bundle
import android.content.pm.PackageManager
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.newwesterndev.mapchat.Adapter.DataAdapter
import com.newwesterndev.mapchat.Model.Model
import com.newwesterndev.mapchat.Model.RxBus

import com.newwesterndev.mapchat.R
import com.newwesterndev.mapchat.Utilities.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_partner_list.*
import org.jetbrains.anko.alert
import java.util.*
import kotlin.collections.ArrayList

class PartnerListFragment : Fragment(), DataAdapter.Listener {

    private var mPartnerList = ArrayList<Model.User>()
    private lateinit  var mDataAdapter: DataAdapter
    private lateinit var mPartnerListInterface: PartnerListInterface
    private var mCompositeDisposable = CompositeDisposable()
    private lateinit var mDisposable: Disposable
    private var newUserDialog = AddNewUserFragment()
    private lateinit var mUtility: Utility
    private var mDistanceFormattedList = ArrayList<Model.Partner>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_partner_list, container, false)

        mUtility = Utility(activity.applicationContext)
        val partnerList = view?.findViewById(R.id.partnerList) as RecyclerView
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(activity)
        partnerList.layoutManager = layoutManager
        partnerList.setHasFixedSize(true)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    newUserDialog.show(fragmentManager, "AddNewUser")
                }
            } else {
                Toast.makeText(activity.applicationContext, "You didn't allow location permissions, sorry", Toast.LENGTH_LONG).show()
            }
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
                    mDistanceFormattedList = mUtility.getPartnersListWithDistanceData(it.users, mPartnerListInterface.getCurrentUser())
                    mDistanceFormattedList.sort()
                    mPartnerList.clear()
                    for (i in mDistanceFormattedList.indices) {
                        mPartnerList.add(Model.User(mDistanceFormattedList[i].username, mDistanceFormattedList[i].latitude,
                                mDistanceFormattedList[i].longitude))
                    }
                    Log.e("old list", it.toString())
                    Log.e("new list", mDistanceFormattedList.toString())
                    mDataAdapter.notifyDataSetChanged()
                })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPause() {
        super.onPause()
        mCompositeDisposable.add(mDisposable)
        mCompositeDisposable.clear()
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
        fun getCurrentUser() : Model.User
    }
}
