package org.gtdev.apps.sensinglight.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import org.gtdev.apps.sensinglight.data.AppDatabase

class ExportDataWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            val database = AppDatabase.getInstance(applicationContext)
            val records = database.recordEntityDao().getRecords()
            for (rec in records) {
                if(rec.locationId != null) {
                    val location = database.locationRecordDao().getLocationByID(rec.locationId)
                    val res = "%d,%d,%f,%f,%f,%f".format(rec.entityId, rec.recordTime.timeInMillis,
                        location.speed, location.altitude, location.latitude, location.longitude)
                    Log.i(TAG, res)
                }
            }
            Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Error exporting database", ex)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "ExportDataWorker"
    }
}