package com.example.inf2007_mad_j1847.model

import com.google.firebase.Timestamp


enum class AppointmentStatus(val wire: String) {
    BOOKED("booked"),
    CANCELLED("cancelled"),
    COMPLETED("completed"); // if need to set

    companion object {
        fun fromWire(value: String?): AppointmentStatus {
            return values().firstOrNull { it.wire == value } ?: BOOKED
        }
    }
}

data class AppointmentSlot(
    val appointmentId: String = "",
    val doctorUid: String = "",
    val patientUid: String = "",
    val date: String = "",        // yyyy-MM-dd
    val timeSlot: String = "",    // HH:mm
    val remark: String = "",
    val status: String = AppointmentStatus.BOOKED.wire,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null

){
    val statusEnum: AppointmentStatus
        get() = AppointmentStatus.fromWire(status)
}