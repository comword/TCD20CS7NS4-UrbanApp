package org.gtdev.apps.sensinglight.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import com.google.android.gms.location.ActivityRecognitionClient

class ActivityRecognisedService : Service() {
    // Reference: https://medium.com/@abhiappmobiledeveloper/android-activity-recognition-api-b7f61847d9dc
    private lateinit var mIntentService: Intent
    private lateinit var mPendingIntent: PendingIntent
    private lateinit var mActivityRecognitionClient: ActivityRecognitionClient
    private var mBinder: IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        val serverInstance: ActivityRecognisedService
            get() = this@ActivityRecognisedService
    }

    override fun onCreate() {
        super.onCreate()
        mActivityRecognitionClient = ActivityRecognitionClient(this)
        mIntentService = Intent(this, DetectedActivitiesIntentService::class.java)
        mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT)
        requestActivityUpdatesButtonHandler()
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    fun requestActivityUpdatesButtonHandler() {
        val task = mActivityRecognitionClient.requestActivityUpdates(
            DETECTION_INTERVAL_IN_MILLISECONDS,
            mPendingIntent)
        task?.addOnSuccessListener {
            Toast.makeText(applicationContext,
                "Successfully requested activity updates",
                Toast.LENGTH_SHORT)
                .show()
        }
        task?.addOnFailureListener {
            Toast.makeText(applicationContext,
                "Requesting activity updates failed to start",
                Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun removeActivityUpdatesButtonHandler() {
        val task = mActivityRecognitionClient.removeActivityUpdates(
            mPendingIntent)
        task?.addOnSuccessListener {
            Toast.makeText(applicationContext,
                "Removed activity updates successfully!",
                Toast.LENGTH_SHORT)
                .show()
        }
        task?.addOnFailureListener {
            Toast.makeText(applicationContext, "Failed to remove activity updates!",
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeActivityUpdatesButtonHandler()
    }

    companion object {
        private val TAG = ActivityRecognisedService::class.java.simpleName
        private const val DETECTION_INTERVAL_IN_MILLISECONDS: Long = 5000
    }

}