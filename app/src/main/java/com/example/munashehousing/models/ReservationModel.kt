package com.example.munashehousing.models

data class Reservation(

    val reference: String = "",

    val propertyId: String = "",

    val userId: String = "",

    val leaseMonths: Int = 6,

    val paymentType: String = "",

    val totalPaid: Int = 0
)