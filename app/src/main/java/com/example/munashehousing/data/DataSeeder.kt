package com.example.munashehousing.data

import com.example.munashehousing.data.database.PropertyEntity
import com.example.munashehousing.data.database.UserEntity
import java.util.UUID

object DataSeeder {
    fun generate50Users(): List<UserEntity> {
        return (1..50).map { i ->
            UserEntity(
                id = UUID.randomUUID().toString(),
                name = "Student User $i",
                email = "cse24-${100 + i}@thuto.bac.ac.bw",
                phone = "+267710000$i",
                role = "STUDENT",
                nationalId = "0000$i",
                guardianName = "Guardian $i"
            )
        }
    }

    fun generate50Listings(): List<PropertyEntity> {
        val areas = listOf("Block 8", "CBD", "Phase 2", "Tlokweng", "Extension 2", "Mogoditshane", "Broadhurst")
        val types = listOf("Studio", "Apartment", "Shared Room", "Single Room", "House")
        val images = listOf(
            "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1480074568708-e7b720bb3f09?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1554995207-c18c203602cb?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80"
        )

        return (1..50).map { i ->
            PropertyEntity(
                id = UUID.randomUUID().toString(),
                title = "${types.random()} in ${areas.random()} #$i",
                location = areas.random(),
                price = (1500..8000).random(),
                type = types.random(),
                amenities = "WiFi, Parking, Security",
                imageUrl = images.random(),
                availableDate = "Available Now",
                deposit = (1000..5000).random(),
                isReserved = false,
                description = "This is a beautiful accommodation located in the heart of Gaborone. Ideal for students.",
                contactNumber = "+267720000$i",
                landlordId = "landlord_${i % 5}"
            )
        }
    }
}
