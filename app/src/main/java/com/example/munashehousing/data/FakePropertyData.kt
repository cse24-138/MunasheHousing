package com.example.munashehousing.data

import androidx.compose.runtime.mutableStateListOf
import com.example.munashehousing.models.Property

object FakePropertyData {

    val properties = mutableStateListOf(
        Property(
            id = "1",
            title = "Modern Studio in Block 8",
            location = "Block 8, Gaborone",
            price = 2800,
            type = "Studio",
            amenities = "WiFi, AC, Private Bath",
            imageUrl = "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=800&q=80",
            interiorImages = listOf(
                "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1484154218962-a197022b5858?auto=format&fit=crop&w=800&q=80"
            ),
            availableDate = "Available Now",
            deposit = 2800,
            description = "A beautiful and quiet studio apartment located in the heart of Block 8. Perfect for students who value privacy and study time."
        ),
        Property(
            id = "2",
            title = "Luxury Apartment - CBD",
            location = "CBD, Gaborone",
            price = 5500,
            type = "Apartment",
            amenities = "WiFi, Gym Access, Pool",
            imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80",
            interiorImages = listOf(
                "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1556912172-45b7abe8b7e1?auto=format&fit=crop&w=800&q=80"
            ),
            availableDate = "1st August",
            deposit = 5500,
            description = "Experience luxury living in the Gaborone CBD. This high-end apartment offers stunning city views and top-notch security."
        ),
        Property(
            id = "3",
            title = "Shared Student House - Phase 2",
            location = "Phase 2, Gaborone",
            price = 1500,
            type = "Shared Room",
            amenities = "WiFi, Shared Kitchen, Laundry",
            imageUrl = "https://images.unsplash.com/photo-1554995207-c18c203602cb?auto=format&fit=crop&w=800&q=80",
            interiorImages = listOf(
                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1493809842364-78817add7ffb?auto=format&fit=crop&w=800&q=80"
            ),
            availableDate = "Immediate",
            deposit = 1000,
            description = "Affordable shared accommodation in Phase 2. Close to main transport routes and shopping centers. Ideal for social students."
        ),
        Property(
            id = "4",
            title = "Bachelor Pad - Tlokweng",
            location = "Tlokweng, Gaborone",
            price = 2200,
            type = "Bachelor",
            amenities = "Parking, Security Fence",
            imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80",
            interiorImages = listOf(
                "https://images.unsplash.com/photo-1499916156354-51101b67797b?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1524758631624-e2822e304c36?auto=format&fit=crop&w=800&q=80"
            ),
            availableDate = "15th July",
            deposit = 1500,
            description = "Neat bachelor pad in a secure yard in Tlokweng. Very close to the border and local amenities."
        ),
        Property(
            id = "5",
            title = "Cozy Room - Extension 2",
            location = "Extension 2, Gaborone",
            price = 1800,
            type = "Single Room",
            amenities = "Quiet Area, WiFi",
            imageUrl = "https://images.unsplash.com/photo-1536376074432-8d4fa5d776bd?auto=format&fit=crop&w=800&q=80",
            interiorImages = listOf(
                "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?auto=format&fit=crop&w=800&q=80"
            ),
            availableDate = "Available Now",
            deposit = 900,
            description = "Comfortable single room in a historic part of Gaborone. Extension 2 offers a peaceful environment for serious students."
        )
    )
}