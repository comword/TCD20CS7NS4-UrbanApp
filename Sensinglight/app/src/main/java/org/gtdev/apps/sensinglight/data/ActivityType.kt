package org.gtdev.apps.sensinglight.data

enum class ActivityType(val value: Int) {
    VEHICLE(0),
    BICYCLE(1),
    FOOT(2),
    STILL(3),
    UNKNOWN(4),
    TILTING(5),
    WALKING(7),
    RUNNING(8);

    companion object {
        private val VALUES = values();
        fun getByValue(value: Int) = VALUES.firstOrNull { it.value == value }
    }
}