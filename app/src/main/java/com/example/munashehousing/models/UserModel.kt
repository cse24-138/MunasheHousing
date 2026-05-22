package com.example.munashehousing.models

enum class UserRole {
    STUDENT, LANDLORD
}

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.STUDENT,
    val nationality: String = "",
    val nationalId: String = "",
    val passportNumber: String = "",
    val guardianName: String = "",
    val guardianPhone: String = "",
    val studentId: String = ""
)