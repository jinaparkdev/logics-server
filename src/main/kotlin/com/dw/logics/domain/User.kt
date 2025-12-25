package com.dw.logics.domain

data class User(
    val id: Long,
    val loginId: String,
    val role: UserRole
)


enum class UserRole {
    Admin, Driver
}