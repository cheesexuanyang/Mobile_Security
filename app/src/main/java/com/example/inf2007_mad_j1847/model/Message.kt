package com.example.inf2007_mad_j1847.model

import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now()
)