package org.gtdev.apps.sensinglight

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import org.gtdev.apps.sensinglight.service.LocationService
import org.gtdev.apps.sensinglight.ui.home.HomeViewModel
import org.gtdev.apps.sensinglight.ui.login.LoginActivity


private const val TAG = "MainActivity"
private const val REQUEST_LOCATION_PERMISSIONS_REQUEST_CODE = 34

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var mLocationServiceBound = false

    // Provides location updates for while-in-use feature.
    var locationService: LocationService? = null

    // Listens for location broadcasts from LocationService.
    private lateinit var mBroadcastReceiver: LocationBroadcastReceiver

    private val homeViewModel: HomeViewModel by viewModels()

    // Monitors connection to the while-in-use service.
    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            mLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locationService = null
            mLocationServiceBound = false
        }
    }

    private inner class LocationBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                LocationService.EXTRA_LOCATION
            )

            if (location != null) {
                homeViewModel.statusCard.visible.value = true
                homeViewModel.countRecord.value = homeViewModel.countRecord.value?.plus(1)
                homeViewModel.statusCard.speed.value = "%.2f".format(location.speed)
                homeViewModel.statusCard.altitude.value = "%.2f".format(location.altitude)
                homeViewModel.statusCard.latitude.value = Location.convert(
                    location.latitude,
                    Location.FORMAT_SECONDS
                )
                homeViewModel.statusCard.longitude.value = Location.convert(
                    location.longitude,
                    Location.FORMAT_SECONDS
                )

//                homeViewModel.saveRecord(location)
            }
        }

    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            Snackbar.make(
                findViewById(R.id.container),
                R.string.permission_rationale,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.ok) {
                    // Request permission
                    requestPermissions()
                }
                .show()
        } else {
            requestPermissions()
        }
    }

    fun requestPermissions() {
        Log.d(TAG, "Request permission")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ),
                REQUEST_LOCATION_PERMISSIONS_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
            window.navigationBarColor = Color.BLACK
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_discover, R.id.navigation_settings
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mBroadcastReceiver = LocationBroadcastReceiver()

        if (!foregroundPermissionApproved()) {
            requestForegroundPermissions()
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            startActivity(intent)
            this.finish()
        }

//        updateButtonState(
//            mSharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
//        )
//        mSharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val serviceIntent = Intent(this, LocationService::class.java)
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mBroadcastReceiver,
            IntentFilter(
                LocationService.ACTION_LOCATION_BROADCAST
            )
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            mBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        if (mLocationServiceBound) {
            unbindService(mServiceConnection)
            mLocationServiceBound = false
        }
//        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }
}