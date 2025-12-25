package com.dw.logics.entity

import com.dw.logics.domain.User
import com.dw.logics.domain.UserRole
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object UserTable : LongIdTable("user") {

    val loginId = varchar("login_id", 255).uniqueIndex()
    val password = varchar("password", 255)
    val role = enumeration("role", UserRole::class).default(UserRole.Driver)
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    var loginId by UserTable.loginId
    var password by UserTable.password
    var role by UserTable.role

    fun toModel() = User(id.value, loginId, role)

    companion object : LongEntityClass<UserEntity>(UserTable)
}