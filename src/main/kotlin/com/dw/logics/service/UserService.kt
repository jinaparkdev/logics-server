package com.dw.logics.service

import com.dw.logics.domain.User
import com.dw.logics.domain.UserRole
import com.dw.logics.entity.UserEntity
import com.dw.logics.entity.UserTable
import com.dw.logics.utils.dbQuery
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val pwEncoder: PasswordEncoder) {

    suspend fun createUser(loginId: String, password: String): User = dbQuery {

        val hashedPassword = pwEncoder.encode(password)
        UserEntity.new {
            this.loginId = loginId
            this.password = hashedPassword
            this.role = UserRole.Driver.name
        }.toModel()
    }

    suspend fun getUser(loginId: String, password: String): User? = dbQuery {
        val userEntity = UserEntity.find { UserTable.loginId eq loginId }.firstOrNull() ?: return@dbQuery null
        return@dbQuery if (pwEncoder.matches(password, userEntity.password)) {
            userEntity.toModel()
        } else {
            null
        }
    }
}
