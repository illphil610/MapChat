package com.newwesterndev.mapchat.Fragments

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.newwesterndev.mapchat.MainActivity
import com.newwesterndev.mapchat.Model.Model
import com.newwesterndev.mapchat.Model.RxBus

import com.newwesterndev.mapchat.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MapFragment : Fragment() {

    private var mMapView: MapView? = null
    //private val mMapView by lazy{
        //activity.findViewById<MapView>(R.id.mapView)
    //}
    private var mapMarkers: ArrayList<Model.User> = ArrayList()
    private lateinit var mMapFragmentInterface: MapFragmentInterface
    private var dataGiven: Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //retainInstance = true
        //mapMarkers = MainActivity.mArrayList

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(activity, "pk.eyJ1IjoiaWxscGhpbDYxMCIsImEiOiJjamVnOWdrYzUyZGQ5MnBsbmgxc2k0dDZhIn0.qPsK72mgu8uZLduuLW4S9Q\n")
        //mapMarkers = MainActivity.mArrayList
        super.onCreate(savedInstanceState)

       // Log.e("main activity list", mapMarkers.toString())

        RxBus.listen(Model.UserList::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    mapMarkers.clear()
                    mapMarkers.addAll(it.users)
                    updateMap(mapMarkers)
                })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_map, container, false)
        val mapView = view?.findViewById<MapView>(R.id.mapView)
        mMapView = mapView!!
        mMapView?.onCreate(savedInstanceState)

        mapMarkers = mMapFragmentInterface.getUserArrayList()
        updateMap(mapMarkers)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mMapView = view.findViewById(R.id.partnerMapView)
        //mMapView.onCreate(savedInstanceState)
        //mapMarkers = MainActivity.mArrayList
        //Log.e("main activyt list", mapMarkers.toString())
        //updateMap(mapMarkers)
        //updateMap(mapMarkers)
        //mapMarkers = MainActivity.mArrayList
    }

    private fun placeUsersOnMap() {
        mMapView?.getMapAsync({
            if (!dataGiven) {
                for (user in MainActivity.mArrayList) {
                    val marker = MarkerOptions()
                    it.addMarker(marker
                            .position(LatLng(user.latitude.toDouble(), user.longitude.toDouble())))
                }
            } else {
                for (user in mapMarkers) {
                    val marker = MarkerOptions()
                    it.addMarker(marker
                            .position(LatLng(user.latitude.toDouble(), user.longitude.toDouble())))
                }
            }
        })
    }

    private fun updateMap(userList: ArrayList<Model.User>) {
        mMapView?.getMapAsync({
            for (user in userList) {
                val marker = MarkerOptions()
                it.addMarker(marker
                        .position(LatLng(user.latitude.toDouble(), user.longitude.toDouble())))
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let { mMapView?.onSaveInstanceState(it) }
    }

    override fun onStart() {
        super.onStart()
        mMapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView?.onStop()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView?.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMapView?.onDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView?.onDestroy()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mMapFragmentInterface = context as MapFragment.MapFragmentInterface
    }

    companion object {
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }

    interface MapFragmentInterface {
        fun getUserArrayList() : ArrayList<Model.User>
    }
}

