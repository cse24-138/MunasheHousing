package com.example.munashehousing.models

data class Property(
    val id: String,
    val title: String,
    val location: String,
    val price: Int,
    val type: String,
    val amenities: String,
    val imageUrl: String, 
    val interiorImages: List<String> = emptyList(),
    val availableDate: String,
    val deposit: Int,
    var isReserved: Boolean = false,
    val description: String = "",
    val contactNumber: String = "+26771000000",
    val landlordId: String = "landlord123" // Added to track ownership
)