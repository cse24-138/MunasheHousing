package com.example.munashehousing.data

import com.example.munashehousing.data.database.*
import com.example.munashehousing.models.Property
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PropertyRepository(val db: AppDatabase) {

    // --- Listings ---
    val allProperties: Flow<List<Property>> = db.propertyDao().getAllProperties().map { entities ->
        entities.map { it.toModel() }
    }

    suspend fun insertProperty(property: Property) {
        db.propertyDao().insertProperty(property.toEntity())
    }

    suspend fun updateProperty(property: Property) {
        db.propertyDao().updateProperty(property.toEntity())
    }

    fun filterProperties(minPrice: Int, maxPrice: Int, location: String): Flow<List<Property>> {
        return db.propertyDao().filterProperties(minPrice, maxPrice, location).map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun getPropertyById(id: String): Property? {
        return db.propertyDao().getPropertyById(id)?.toModel()
    }

    // --- Reservations ---
    val allReservations: Flow<List<ReservationEntity>> = db.reservationDao().getAllReservations()

    suspend fun addReservation(reservation: ReservationEntity) {
        // Check if already reserved to prevent double booking
        val existing = db.reservationDao().getReservationByProperty(reservation.propertyId)
        if (existing == null) {
            db.reservationDao().insertReservation(reservation)
            // Mark property as reserved
            val propertyEntity = db.propertyDao().getPropertyById(reservation.propertyId)
            propertyEntity?.let {
                db.propertyDao().updateProperty(it.copy(isReserved = true))
            }
        }
    }

    suspend fun isPropertyReserved(propertyId: String): Boolean {
        return db.reservationDao().getReservationByProperty(propertyId) != null
    }

    fun getReservationsForUser(userId: String): Flow<List<ReservationEntity>> {
        return db.reservationDao().getReservationsForUser(userId)
    }

    // --- Users ---
    suspend fun registerUser(user: UserEntity) {
        db.userDao().insertUser(user)
    }

    suspend fun loginUser(email: String): UserEntity? {
        return db.userDao().getUserByEmail(email)
    }

    // --- Preferences ---
    suspend fun getPreferences(userId: String): PreferenceEntity? {
        return db.preferenceDao().getPreferences(userId)
    }

    suspend fun savePreferences(preference: PreferenceEntity) {
        db.preferenceDao().savePreferences(preference)
    }

    // --- Seeding ---
    suspend fun seedDatabase() {
        if (db.propertyDao().getPropertyCount() < 50) {
            val listings = DataSeeder.generate50Listings()
            listings.forEach { db.propertyDao().insertProperty(it) }
        }
        if (db.userDao().getUserCount() < 50) {
            val users = DataSeeder.generate50Users()
            db.userDao().insertAll(users)
        }
    }
}

// Mappers to convert between Room Entities and UI Models
fun PropertyEntity.toModel() = Property(
    id = id, title = title, location = location, price = price,
    type = type, amenities = amenities, imageUrl = imageUrl,
    availableDate = availableDate, deposit = deposit,
    isReserved = isReserved, description = description,
    contactNumber = contactNumber, landlordId = landlordId
)

fun Property.toEntity() = PropertyEntity(
    id = id, title = title, location = location, price = price,
    type = type, amenities = amenities, imageUrl = imageUrl,
    availableDate = availableDate, deposit = deposit,
    isReserved = isReserved, description = description,
    contactNumber = contactNumber, landlordId = landlordId
)
