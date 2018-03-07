package com.newwesterndev.mapchat.Fragments

import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.maps.MapView

import com.newwesterndev.mapchat.R
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(activity, "pk.eyJ1IjoiaWxscGhpbDYxMCIsImEiOiJjamVnOWdrYzUyZGQ5MnBsbmgxc2k0dDZhIn0.qPsK72mgu8uZLduuLW4S9Q\n")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater?.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (view != null) {
            mapView = view.findViewById(R.id.partnerMapView)
        }
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync({
            it.setStyle(Style.MAPBOX_STREETS)
        })

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

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let { partnerMapView.onSaveInstanceState(it) }
    }

    companion object {
        fun newInstance(): MapFragment {
            //val args = Bundle()
            //fragment.arguments = args
            return MapFragment()
        }
    }
}

