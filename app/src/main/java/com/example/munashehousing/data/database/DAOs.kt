package com.example.munashehousing.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)
}

@Dao
interface PropertyDao {
    @Query("SELECT * FROM listings")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM listings WHERE id = :id")
    suspend fun getPropertyById(id: String): PropertyEntity?

    @Query("SELECT * FROM listings WHERE price BETWEEN :minPrice AND :maxPrice AND location LIKE '%' || :location || '%'")
    fun filterProperties(minPrice: Int, maxPrice: Int, location: String): Flow<List<PropertyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity)

    @Update
    suspend fun updateProperty(property: PropertyEntity)

    @Query("SELECT COUNT(*) FROM listings")
    suspend fun getPropertyCount(): Int
}

@Dao
interface ReservationDao {
    @Query("SELECT * FROM reservations")
    fun getAllReservations(): Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE studentId = :studentId")
    fun getReservationsForUser(studentId: String): Flow<List<ReservationEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertReservation(reservation: ReservationEntity)

    @Query("SELECT * FROM reservations WHERE propertyId = :propertyId")
    suspend fun getReservationByProperty(propertyId: String): ReservationEntity?
}

@Dao
interface PreferenceDao {
    @Query("SELECT * FROM preferences WHERE userId = :userId")
    suspend fun getPreferences(userId: String): PreferenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(preference: PreferenceEntity)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE (senderId = :userId AND receiverName = :receiver) OR (senderId = :receiver AND receiverName = :userId) ORDER BY timestamp ASC")
    fun getChatHistory(userId: String, receiver: String): Flow<List<MessageEntity>>

    @Insert
    suspend fun insertMessage(message: MessageEntity)
}
