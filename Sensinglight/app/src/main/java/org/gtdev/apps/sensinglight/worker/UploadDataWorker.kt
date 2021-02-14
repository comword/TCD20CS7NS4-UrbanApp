package org.gtdev.apps.sensinglight.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.coroutineScope
import org.gtdev.apps.sensinglight.data.AppDatabase

class UploadDataWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            val appDatabase = AppDatabase.getInstance(applicationContext)
            val database = Firebase.database.reference

            val records = appDatabase.recordEntityDao().getNonUploadedRecords()
            for (rec in records) {
                if(rec.locationId != null) {
                    val location = appDatabase.locationRecordDao().getLocationByID(rec.locationId)
                    val key = database.child("records").push().key
                    if (key == null) {
                        Log.w(TAG, "Couldn't get push key.")
                        Result.failure()
                    }

                    val locId = location.entityId
                    val recId = rec.entityId
                    val locationValues = location.toMap(locId)
                    val recValues = rec.toMap()

                    val childUpdates = hashMapOf<String, Any>(
                        "/locations/$locId/$key" to locationValues,
                        "/records/$recId/$key" to recValues
                    )

                    database.updateChildren(childUpdates)
                }

                rec.isUploaded = true
            }
            appDatabase.recordEntityDao().updateRecords(records)
            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Error uploading database", ex)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "UploadDataWorker"
    }
}