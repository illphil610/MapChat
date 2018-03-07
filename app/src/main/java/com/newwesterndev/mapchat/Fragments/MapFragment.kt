package com.newwesterndev.mapchat.Fragments

import android.os.Bundle
import android.app.Fragment
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.newwesterndev.mapchat.MainActivity
import com.newwesterndev.mapchat.Model.Model
import com.newwesterndev.mapchat.Model.RxBus

import com.newwesterndev.mapchat.R
import com.newwesterndev.mapchat.Utilities.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private var mapMarkers : ArrayList<Model.User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(activity, "pk.eyJ1IjoiaWxscGhpbDYxMCIsImEiOiJjamVnOWdrYzUyZGQ5MnBsbmgxc2k0dDZhIn0.qPsK72mgu8uZLduuLW4S9Q\n")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        RxBus.listen(Model.UserList::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    mapMarkers.clear()
                    mapMarkers.addAll(it.users)
                    mapView.getMapAsync({
                        it.setStyle(Style.MAPBOX_STREETS)
                        for (user in mapMarkers) {
                            val marker = MarkerOptions()
                            it.addMarker(marker
                                    .position(LatLng(user.latitude.toDouble(), user.longitude.toDouble())))
                        }
                    })
                })
        return inflater?.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.partnerMapView)
        mapView.onCreate(savedInstanceState)
        placeUsersOnMap()
    }

    private fun placeUsersOnMap() {
        mapView.getMapAsync({
            it.setStyle(Style.MAPBOX_STREETS)
            for (user in MainActivity.mArrayList) {
                val marker = MarkerOptions()
                Log.e("List I Wanna populate", MainActivity.mArrayList.toString())
                it.addMarker(marker
                        .position(LatLng(user.latitude.toDouble(), user.longitude.toDouble())))
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)

        mapView.getMapAsync({
            it.setStyle(Style.MAPBOX_STREETS)
        })

        Log.e("HELLO", "testing")
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    companion object {
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }
}

