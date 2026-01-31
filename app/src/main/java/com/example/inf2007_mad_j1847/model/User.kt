package com.example.inf2007_mad_j1847.model

enum class Role(val displayName: String) {
    PATIENT("Patient"),
    DOCTOR("Doctor"),
    ADMIN("Admin"),

}

data class User(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val role: Role = Role.PATIENT , // default value will be patient, other roles are "DOCTOR", "ADMIN"
)