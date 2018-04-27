package edu.temple.mapchat.Fragments

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import edu.temple.mapchat.Model.Model
import edu.temple.mapchat.Model.RxBus

import edu.temple.mapchat.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MapFragment : Fragment() {

    private var mMapView: MapView? = null
    private var mapMarkers: ArrayList<Model.User> = ArrayList()
    private lateinit var mMapFragmentInterface: MapFragmentInterface
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var mDisposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(activity, "pk.eyJ1IjoiaWxscGhpbDYxMCIsImEiOiJjamVnOWdrYzUyZGQ5MnBsbmgxc2k0dDZhIn0.qPsK72mgu8uZLduuLW4S9Q\n")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_map, container, false)
        mMapView = view?.findViewById<MapView>(R.id.mapView)
        mMapView?.onCreate(savedInstanceState)
        mapMarkers = mMapFragmentInterface.getUserArrayList()
        updateMap(mapMarkers)
        return view
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
        mCompositeDisposable.add(mDisposable)
        mCompositeDisposable.clear()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()

        mDisposable = RxBus.listen(Model.UserList::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    mapMarkers.clear()
                    mapMarkers.addAll(it.users)
                    updateMap(mapMarkers)
                    Log.e("Markers", "markers updated")
                })
    }

    override fun onPause() {
        super.onPause()
        mMapView?.onPause()
        mCompositeDisposable.add(mDisposable)
        mCompositeDisposable.clear()
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
        mMapFragmentInterface = context as MapFragmentInterface
    }

    private fun updateMap(userList: ArrayList<Model.User>) {
        mMapView?.getMapAsync({
            for (user in userList) {
                val marker = MarkerOptions()
                it.addMarker(marker.position(LatLng(user.latitude, user.longitude)))
            }
        })
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

