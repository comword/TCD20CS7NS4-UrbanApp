package org.gtdev.apps.sensinglight.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "data_packs")
data class DataPacksEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var PackId: Long = 0,

    @ColumnInfo(name = "start_time")
    val startTime: Calendar = Calendar.getInstance(),

    @ColumnInfo(name = "end_time")
    val endTime: Calendar = Calendar.getInstance(),
)