package com.dw.logics.entity

import com.dw.logics.domain.Driver
import com.dw.logics.domain.DriverStatus
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object DriverTable : LongIdTable("driver") {
    val name = varchar("name", 255)
    val phoneNumber = varchar("phone_number", 20).uniqueIndex()
    val vehicleNumber = varchar("vehicle_number", 20)
    val status = enumeration("status", DriverStatus::class).default(DriverStatus.AVAILABLE)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp())
}

class DriverEntity(id: EntityID<Long>) : LongEntity(id) {
    var name by DriverTable.name
    var phoneNumber by DriverTable.phoneNumber
    var vehicleNumber by DriverTable.vehicleNumber
    var status by DriverTable.status
    var createdAt by DriverTable.createdAt
    var updatedAt by DriverTable.updatedAt

    fun toModel()= Driver(
        id = this.id.value,
        name = this.name,
        phoneNumber = this.phoneNumber,
        vehicleNumber = this.vehicleNumber,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

    companion object : LongEntityClass<DriverEntity>(DriverTable)
}