package com.dw.logics.config

import com.dw.logics.domain.UserRole
import com.dw.logics.entity.UserEntity
import com.dw.logics.entity.UserTable
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)
    private val passwordEncoder = BCryptPasswordEncoder()

    override fun run(args: ApplicationArguments?) {
        transaction {
            initializeAdminUser()
        }
    }

    private fun initializeAdminUser() {
        val adminLoginId = "admin"

        // admin 사용자가 이미 존재하는지 확인
        val existingAdmin = UserEntity.find { UserTable.loginId eq adminLoginId }.firstOrNull()

        if (existingAdmin == null) {
            // admin 사용자가 없으면 생성
            val hashedPassword = passwordEncoder.encode("admin123") // 기본 비밀번호

            UserEntity.new {
                loginId = adminLoginId
                password = hashedPassword
                role = UserRole.Admin
            }

            logger.info("관리자 계정 생성이 완료되었습니다: $adminLoginId")
        } else {
            logger.info("관리자 계정이 이미 존재합니다. 생성을 건너뜁니다: $adminLoginId")
        }
    }
}

