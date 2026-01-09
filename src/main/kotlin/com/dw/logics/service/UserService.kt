package com.dw.logics.service

import com.dw.logics.domain.User
import com.dw.logics.domain.UserRole
import com.dw.logics.entity.UserEntity
import com.dw.logics.entity.UserTable
import com.dw.logics.exception.DuplicateCredentialException
import com.dw.logics.exception.InvalidCredentialsException
import com.dw.logics.utils.dbQuery
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val pwEncoder: PasswordEncoder) {

    suspend fun createUser(loginId: String, password: String): User = dbQuery {

        val available = UserEntity.find { UserTable.loginId eq loginId }.count() == 0L

        if (!available) {
            throw DuplicateCredentialException()
        }

        val hashedPassword = pwEncoder.encode(password)

        UserEntity.new {
            this.loginId = loginId
            this.password = hashedPassword
            this.role = UserRole.Driver.name
        }.toModel()
    }

    suspend fun getUser(loginId: String, password: String): User = dbQuery {
        UserEntity.find { UserTable.loginId eq loginId }.firstOrNull()
            ?.takeIf { pwEncoder.matches(password, it.password) }
            ?.toModel()
            ?: throw InvalidCredentialsException()
    }
}
