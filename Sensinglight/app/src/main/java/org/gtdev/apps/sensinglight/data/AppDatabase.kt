package org.gtdev.apps.sensinglight.data;

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [LocationEntity::class, RecordEntity::class,
    DataPacksEntity::class, ActivityEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationRecordDao(): LocationRecordDao
    abstract fun recordEntityDao(): RecordEntityDao
    abstract fun dataPacksDao(): DataPacksDao
    abstract fun activityDao(): ActivityDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "sensing-light-db")
                .addCallback(
                        object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
//                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
//                            WorkManager.getInstance(context).enqueue(request)
                            }
                        }
                ).addMigrations(MIGRATION_1_2)
                .build()
        }

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE record ADD COLUMN is_uploaded INTEGER NOT NULL DEFAULT(0)")
            }
        }
    }

    @Dao
    interface LocationRecordDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAll(locations: List<LocationEntity>)

        @Insert
        suspend fun insert(location: LocationEntity): Long

        @Query("SELECT * FROM location")
        fun getLocationRecords(): List<LocationEntity>

        @Query("SELECT * FROM location WHERE id=:mID LIMIT 1")
        fun getLocationByID(mID: Long): LocationEntity

        @Query("SELECT * FROM location ORDER BY id")
        fun getLocationLiveData(): LiveData<List<LocationEntity>>
    }

    @Dao
    interface RecordEntityDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAll(records: List<RecordEntity>)

        @Insert
        suspend fun insert(record: RecordEntity): Long

        @Query("SELECT * FROM record")
        fun getRecords(): List<RecordEntity>

        @Query("SELECT * FROM record WHERE is_uploaded=0")
        fun getNonUploadedRecords(): List<RecordEntity>

        @Update()
        fun updateRecords(records: List<RecordEntity>)

        @Update()
        fun updateRecord(records: RecordEntity)
    }

    @Dao
    interface DataPacksDao {
        @Insert
        suspend fun insert(data: DataPacksEntity): Long
        @Update
        suspend fun update(data: DataPacksEntity)
        @Delete
        suspend fun delete(data: DataPacksEntity)
    }

    @Dao
    interface ActivityDao {
        @Insert
        suspend fun insert(data: ActivityEntity)

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAll(records: List<ActivityEntity>)

        @Update
        suspend fun update(data: ActivityEntity)
        @Delete
        suspend fun delete(data: ActivityEntity)

        @Query("SELECT MAX(id) FROM activity")
        fun getLastId(): Long
    }
}
