package org.gtdev.apps.sensinglight.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import org.gtdev.apps.sensinglight.R
import org.gtdev.apps.sensinglight.data.AppDatabase
import org.gtdev.apps.sensinglight.data.LocationEntity


class DiscoverFragment : Fragment(), OnMapReadyCallback {

    private lateinit var discoverViewModel: DiscoverViewModel
    private lateinit var mMap: GoogleMap

    private var appDatabase: AppDatabase? = null
//    private val database = Firebase.database.reference

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        appDatabase = AppDatabase.getInstance(requireActivity().applicationContext)
        discoverViewModel =
            ViewModelProvider(this).get(DiscoverViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_discover, container, false)
//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        discoverViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        val mapView = root.findViewById(R.id.map) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        try {
            MapsInitializer.initialize(activity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapView.getMapAsync(this)
        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
//        mMap.setMyLocationEnabled(true);

        val dublin = LatLng(53.354089, -6.257097)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dublin, 15F))

        appDatabase!!.locationRecordDao().getLocationLiveData().observe(this,
            {
                val points = ArrayList<LatLng>();
                for (loc in it)
                    points.add(LatLng(loc.latitude, loc.longitude))

                val polylineOptions = PolylineOptions()
                        .clickable(false)
                        .addAll(points)
                mMap.addPolyline(polylineOptions)
            })
    }
}