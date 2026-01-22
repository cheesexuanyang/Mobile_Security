package com.example.inf2007_mad_j1847.model

data class User(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val role: String = "PATIENT", // default value will be patient, other roles are "DOCTOR", "ADMIN"
)
