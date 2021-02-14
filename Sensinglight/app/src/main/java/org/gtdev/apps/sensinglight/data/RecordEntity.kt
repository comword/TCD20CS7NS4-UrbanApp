package org.gtdev.apps.sensinglight.data

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.*
import java.util.Calendar

@Entity(
    tableName = "record",
    foreignKeys = [
        ForeignKey(entity = LocationEntity::class, parentColumns = ["id"], childColumns = ["location_id"]),
//        ForeignKey(entity = ActivityEntity::class, parentColumns = ["id"], childColumns = ["activity_id"])
    ],
    indices = [Index("record_time", "location_id")]
)
data class RecordEntity (
    @ColumnInfo(name = "location_id")
    @Nullable
    val locationId: Long? = null,

    @ColumnInfo(name = "activity_id")
    @Nullable
    val activityId: Long? = null,

    @ColumnInfo(name = "record_time")
    @NonNull
    val recordTime: Calendar = Calendar.getInstance(),

    @ColumnInfo(name = "is_uploaded")
    @NonNull
    var isUploaded: Boolean = false
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var entityId: Long = 0

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "locationId" to locationId,
            "activityID" to activityId,
            "recordTime" to recordTime.timeInMillis
        )
    }

}