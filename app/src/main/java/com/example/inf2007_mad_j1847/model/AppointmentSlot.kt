package com.example.inf2007_mad_j1847.model

import com.google.firebase.Timestamp

data class AppointmentSlot(
    val doctorUid: String = "",
    val PatientUid: String = "",
    val date: String = "",        // yyyy-MM-dd
    val timeSlot: String = "",    // HH:mm
    val status: String = "booked",
    val createdAt: Timestamp? = null

)
