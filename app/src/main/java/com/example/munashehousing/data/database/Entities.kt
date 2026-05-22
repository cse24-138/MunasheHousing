package com.example.munashehousing.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val nationalId: String,
    val guardianName: String
)

@Entity(tableName = "listings")
data class PropertyEntity(
    @PrimaryKey val id: String,
    val title: String,
    val location: String,
    val price: Int,
    val type: String,
    val amenities: String,
    val imageUrl: String,
    val availableDate: String,
    val deposit: Int,
    val isReserved: Boolean = false,
    val description: String,
    val contactNumber: String,
    val landlordId: String
)

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey val referenceNumber: String,
    val propertyId: String,
    val studentId: String,
    val amountPaid: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "preferences")
data class PreferenceEntity(
    @PrimaryKey val userId: String,
    val minPrice: Int = 0,
    val maxPrice: Int = 10000,
    val preferredLocation: String = "",
    val preferredType: String = ""
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderId: String,
    val receiverName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSeen: Boolean = false
)
