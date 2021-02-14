package org.gtdev.apps.sensinglight.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel internal constructor(): ViewModel() {

//    fun saveRecord(location: Location) {
//        viewModelScope.launch {
//            val locEnt = LocationEntity(location.speed, location.latitude,
//                location.longitude, location.altitude)
//
//            val recEnt = RecordEntity(locationRecordDao.insert(locEnt))
//            recordEntityDao.insert(recEnt)
//        }
//    }

//    fun getLocationRecords() : LiveData<List<LocationEntity>>{
//        return locationRecordDao.getLocationRecords()
//    }

    val btnEnabled = MutableLiveData<Boolean>(false)
    val countRecord = MutableLiveData<Int>(0)

    class StatusCard {
        val speed = MutableLiveData<String>()
        val altitude = MutableLiveData<String>()
        val latitude = MutableLiveData<String>()
        val longitude = MutableLiveData<String>()
        val visible = MutableLiveData<Boolean>(false)
    }

    var statusCard = StatusCard()

}