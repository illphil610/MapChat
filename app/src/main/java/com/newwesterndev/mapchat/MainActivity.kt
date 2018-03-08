package com.newwesterndev.mapchat

import android.Manifest
import android.app.Activity
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

class MainActivity : Activity(), PartnerListFragment.PartnerListInterface, MapFragment.MapFragmentInterface {

    private var mCompositeDisposable = CompositeDisposable()
    private val mUtility = Utility(this)
    private lateinit var mDisposable: Disposable
    private lateinit var mRequestInterface: RequestInterface
    private lateinit var partnerListFragment: PartnerListFragment
    private lateinit var mapFragment: MapFragment
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION), 10)

        val mTwoPainz = findViewById<MapView>(R.id.partnerMapView) != null
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        partnerListFragment = PartnerListFragment.newInstance()
        mapFragment = MapFragment.newInstance()
        //fragmentManager.inTransaction { replace(R.id.mapchat_nav_fragment, PartnerListFragment.newInstance()) }
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mapchat_nav_fragment, partnerListFragment)
        transaction.commit()

        if (mTwoPainz) {
            //fragmentManager.inTransaction { add(R.id.partnerMapView, MapFragment.newInstance())}
            fragmentManager.executePendingTransactions()
            val transaction2 = fragmentManager.beginTransaction()
            transaction2.replace(R.id.partnerMapView, mapFragment)
                    .commit()
        }

        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        , 10) }
        } else {
            mFusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    mUtility.showToast(this, it.toString())
                } else {
                    mUtility.showToast(this, "Nah Fam")
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                mFusedLocationClient.lastLocation.addOnSuccessListener {
                    if (it != null) {
                        mUtility.showToast(this, it.toString())
                    } else {
                        mUtility.showToast(this, "Nah Fam")
                    }
                }
            }
        }
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

    private fun pollServer(requestInterface: RequestInterface): Disposable {
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

    override fun userItemSelected() {
        //fragmentManager.inTransaction {
        //add(R.id.mapchat_nav_fragment, MapFragment.newInstance())
        //}
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mapchat_nav_fragment, MapFragment.newInstance())
                .addToBackStack(null)
                .commit()
        fragmentManager.executePendingTransactions()
    }

    override fun getUserArrayList(): ArrayList<Model.User> {
        return mArrayList
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    companion object {
        var mArrayList: ArrayList<Model.User> = ArrayList()
    }
}
