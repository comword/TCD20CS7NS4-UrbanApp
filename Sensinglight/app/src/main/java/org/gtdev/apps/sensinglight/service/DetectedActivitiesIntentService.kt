package org.gtdev.apps.sensinglight.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.gtdev.apps.sensinglight.data.ActivityEntity
import org.gtdev.apps.sensinglight.data.ActivityType
import org.gtdev.apps.sensinglight.data.AppDatabase
import org.gtdev.apps.sensinglight.data.RecordEntity

class DetectedActivitiesIntentService : IntentService(TAG) {

    private var appDatabase: AppDatabase? = null
    private val database = Firebase.database.reference

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
        appDatabase = AppDatabase.getInstance(applicationContext)
    }

    override fun onHandleIntent(intent: Intent?) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            handleDetectedActivities(result.probableActivities)
        }
    }

//    override fun onHandleWork(intent: Intent) {
//        if (ActivityRecognitionResult.hasResult(intent)) {
//            val result = ActivityRecognitionResult.extractResult(intent)
//            handleDetectedActivities(result.probableActivities)
//        }
//    }

    private fun handleDetectedActivities(probableActivities: List<DetectedActivity>) {
//        val arrActivityEntity = ArrayList<ActivityEntity>()
        val actId = appDatabase!!.activityDao().getLastId() + 1
        for (activity in probableActivities) {
            val confidence = activity.confidence
            when (activity.type) {
                DetectedActivity.IN_VEHICLE -> {
                    Log.d(TAG, "In Vehicle: $confidence")
                }
                DetectedActivity.ON_BICYCLE -> {
                    Log.d(TAG, "On Bicycle: $confidence")
                }
                DetectedActivity.ON_FOOT -> {
                    Log.d(TAG, "On Foot: $confidence")
                }
                DetectedActivity.RUNNING -> {
                    Log.d(TAG, "Running: $confidence")
                }
                DetectedActivity.STILL -> {
                    Log.d(TAG, "Still: $confidence")
                }
                DetectedActivity.TILTING -> {
                    Log.d(TAG, "Tilting: $confidence")
                }
                DetectedActivity.WALKING -> {
                    Log.d(TAG, "Walking: $confidence")
                }
                DetectedActivity.UNKNOWN -> {
                    Log.d(TAG, "Unknown: " + activity.confidence)
                }
            }
//            val actEntity = ActivityEntity(
//                recId,
//                ActivityType.getByValue(activity.type) ?: ActivityType.UNKNOWN, confidence)
//            arrActivityEntity.add(actEntity)
        }
        GlobalScope.launch {
//            appDatabase!!.activityDao().insertAll(arrActivityEntity)
            val lastActivity = getProbableActivity(probableActivities)
            val actEnt = ActivityEntity(actId,
                ActivityType.getByValue(lastActivity.type) ?: ActivityType.UNKNOWN,
                lastActivity.confidence)
            appDatabase!!.activityDao().insert(actEnt)
            val recEnt = RecordEntity(null, actId)
            appDatabase!!.recordEntityDao().insert(recEnt)

            //firebase upload
            val key = database.child("activity").push().key
            val actValues = actEnt.toMap()
            val recValues = recEnt.toMap()

            val childUpdates = hashMapOf<String, Any>(
                "/real-activity/$key" to actValues,
                "/real-records/$key" to recValues,
                "/real-latest/activity" to actValues
            )

            database.updateChildren(childUpdates).addOnCompleteListener {
                recEnt.isUploaded = true
                GlobalScope.launch {
                    appDatabase!!.recordEntityDao().updateRecord(recEnt)
                }
            }
        }.start()
    }

    fun getProbableActivity(detectedActivities: List<DetectedActivity>): DetectedActivity {
        var highestConfidence = 0
        var mostLikelyActivity = DetectedActivity(DetectedActivity.UNKNOWN, 0)
        for (da in detectedActivities) {
            if (da.type != DetectedActivity.TILTING || da.type != DetectedActivity.UNKNOWN) {
                if (highestConfidence < da.confidence) {
                    highestConfidence = da.confidence
                    mostLikelyActivity = da
                }
            }
        }
        return mostLikelyActivity
    }

    companion object {
        private val TAG = DetectedActivitiesIntentService::class.java.simpleName
    }
}