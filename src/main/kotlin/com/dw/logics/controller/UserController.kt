package com.dw.logics.controller

import com.dw.logics.domain.User
import com.dw.logics.service.UserService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CreateUserRequest(
    @field:NotBlank(message = "로그인 아이디는 필수입니다")
    val loginId: String,

    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
        message = "비밀번호는 대문자, 소문자, 숫자를 모두 포함해야 합니다"
    )
    val password: String
)

@RestController
@RequestMapping("/api")
class UserController(private val service: UserService) {

    @PostMapping("/admin/user")
    suspend fun createUser(@RequestBody @Valid request: CreateUserRequest): ResponseEntity<User> {

        val (loginId, password) = request
        val user = service.createUser(loginId, password)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }
}