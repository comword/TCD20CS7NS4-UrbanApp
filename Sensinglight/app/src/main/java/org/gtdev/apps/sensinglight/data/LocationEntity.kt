package org.gtdev.apps.sensinglight.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "location")
data class LocationEntity (
    @NonNull
    val speed: Float,
    @NonNull
    val latitude: Double,
    @NonNull
    val longitude: Double,
    @NonNull
    val altitude: Double,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var entityId: Long = 0,
) {
    @Exclude
    fun toMap(locId: Long): Map<String, Any?> {
        return mapOf(
            "id" to locId,
            "speed" to speed,
            "latitude" to latitude,
            "longitude" to longitude,
            "altitude" to altitude,
        )
    }
}