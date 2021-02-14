package org.gtdev.apps.sensinglight.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "activity", primaryKeys = ["id", "activity"])
data class ActivityEntity (
//    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var entityId: Long = 0,

    @ColumnInfo(name = "activity")
    var activity: ActivityType,

    @ColumnInfo
    var confidence: Int
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to entityId,
            "activity" to activity,
            "confidence" to confidence
        )
    }
}