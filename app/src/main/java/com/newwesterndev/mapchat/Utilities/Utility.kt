package com.newwesterndev.mapchat.Utilities

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.mapbox.mapboxsdk.geometry.LatLng
import com.newwesterndev.mapchat.MainActivity
import com.newwesterndev.mapchat.Model.Model
import com.newwesterndev.mapchat.Model.RxBus
import com.newwesterndev.mapchat.Network.RequestInterface
import com.newwesterndev.mapchat.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Utility(context: Context) {

    val mContext = context
    private var mUserArrayList: ArrayList<Model.User> = ArrayList()

    fun loadJSON(): RequestInterface {
        return Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.kamorris))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)
    }

    fun showToast(content: Context, message: String) {
        Toast.makeText(content, message, Toast.LENGTH_LONG).show()
    }

    fun getArrayList(): ArrayList<Model.User> {
        return mUserArrayList
    }

    fun getPartnersListWithDistanceData(arrayList: ArrayList<Model.User>, currentUsers: Model.User): ArrayList<Model.Partner> {
        var partnersList: ArrayList<Model.Partner> = ArrayList()
        val currentLocation = Location("Current Users Location")
        currentLocation.latitude = currentUsers.latitude
        currentLocation.longitude = currentUsers.longitude

        val tempLocation = Location("Temp")

        for (user in arrayList) {
            tempLocation.latitude = user.latitude
            tempLocation.longitude = user.longitude
            val distance = currentLocation.distanceTo(tempLocation)
            partnersList.add(Model.Partner(user.username, user.latitude, user.longitude, distance))
        }
        return partnersList
    }
}