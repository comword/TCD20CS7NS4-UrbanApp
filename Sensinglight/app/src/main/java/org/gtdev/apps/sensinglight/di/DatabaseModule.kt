/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gtdev.apps.sensinglight.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.gtdev.apps.sensinglight.data.AppDatabase
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideLocationRecordDao(appDatabase: AppDatabase): AppDatabase.LocationRecordDao {
        return appDatabase.locationRecordDao()
    }

    @Provides
    fun provideRecordEntityDao(appDatabase: AppDatabase): AppDatabase.RecordEntityDao {
        return appDatabase.recordEntityDao()
    }

    @Provides
    fun provideDataPacksDao(appDatabase: AppDatabase): AppDatabase.DataPacksDao {
        return appDatabase.dataPacksDao()
    }
}
