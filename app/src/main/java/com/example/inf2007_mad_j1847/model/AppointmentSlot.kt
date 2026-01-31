package com.example.inf2007_mad_j1847.model

import com.google.firebase.Timestamp

data class AppointmentSlot(
    val appointmentId: String = "",
    val doctorUid: String = "",
    val patientUid: String = "",
    val date: String = "",        // yyyy-MM-dd
    val timeSlot: String = "",    // HH:mm
    val remark: String = "",
    val status: String = "booked",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null

)