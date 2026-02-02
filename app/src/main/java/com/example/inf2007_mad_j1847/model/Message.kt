package com.example.inf2007_mad_j1847.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

enum class MessageType {
    TEXT,
    MEDIA,
    LIVE_LOCATION
}

data class Message(
    var id: String = "",
    val senderId: String = "",

    // TEXT or MEDIA (stored as String for Firestore)
    val type: String = MessageType.TEXT.name,

    // Used when type == TEXT
    val text: String = "",

    // Used when type == MEDIA (Firebase Storage download URL)
    val mediaUrl: String? = null,

    // Optional metadata to help UI display
    val fileName: String? = null,
    val mimeType: String? = null,

    val timestamp: Timestamp = Timestamp.now(),

    // Live Location
    val latitude: Double? = null,
    val longitude: Double? = null,

    @get:PropertyName("isLive")
    @set:PropertyName("isLive")
    var isLive: Boolean = false
)
