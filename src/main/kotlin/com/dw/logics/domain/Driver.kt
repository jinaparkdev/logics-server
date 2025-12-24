package com.dw.logics.domain

import java.time.Instant

data class Driver(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val vehicleNumber: String,
    val status: DriverStatus,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class DriverStatus {
    AVAILABLE,
    UNAVAILABLE
}