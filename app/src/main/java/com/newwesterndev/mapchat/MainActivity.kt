package com.newwesterndev.mapchat

import android.app.Activity
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.graphics.ColorSpace
import android.os.Bundle
import android.util.Log
import com.mapbox.mapboxsdk.maps.MapView
import com.newwesterndev.mapchat.Fragments.MapFragment
import com.newwesterndev.mapchat.Fragments.PartnerListFragment
import com.newwesterndev.mapchat.Model.Model
import com.newwesterndev.mapchat.Model.RxBus
import com.newwesterndev.mapchat.Network.RequestInterface
import com.newwesterndev.mapchat.Utilities.Utility
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : Activity() {

    private var mCompositeDisposable = CompositeDisposable()
    private val mUtility = Utility(this)
    private lateinit var mDisposable: Disposable
    private lateinit var mRequestInterface: RequestInterface
    //var mArrayList: ArrayList<Model.User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mTwoPainz = findViewById<MapView>(R.id.partnerMapView) != null
        Log.e("TWO PAINZ", mTwoPainz.toString())

        fragmentManager.inTransaction {
            replace(R.id.mapchat_nav_fragment, PartnerListFragment.newInstance())
        }

        if (mTwoPainz) {
            fragmentManager.inTransaction {
                replace(R.id.partnerMapView, MapFragment.newInstance())
            }
        }
    }

    private inline fun FragmentManager.inTransaction(
            func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    override fun onResume() {
        super.onResume()
        mRequestInterface = mUtility.loadJSON()
        mDisposable = pollServer(mRequestInterface)
    }

    override fun onStop() {
        super.onStop()
        mUtility.clearDisposables(mCompositeDisposable, mDisposable)
    }

    private fun pollServer(requestInterface: RequestInterface) : Disposable {
        return Observable.interval(30, TimeUnit.SECONDS)
                .startWith(0)
                .flatMap { requestInterface.getUsers() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
    }

    private fun handleResponse(userList: List<Model.User>) {
        mArrayList = ArrayList(userList)
        RxBus.publish(Model.UserList(mArrayList))
    }

    private fun handleError(error: Throwable) {
        Log.d(MainActivity::class.java.simpleName, error.localizedMessage)
    }

    companion object {
        var mArrayList: ArrayList<Model.User> = ArrayList()
    }
}
