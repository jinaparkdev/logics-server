package com.dw.logics.controller

import com.dw.logics.domain.User
import com.dw.logics.handler.JwtTokenProvider
import com.dw.logics.service.UserService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


data class SignUpRequest(
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

data class SignInRequest(val loginId: String, val password: String)
data class SignInResponse(val accessToken: String, val refreshToken: String, val user: User)

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService
) {

    @PostMapping("/sign-up")
    suspend fun signUp(@RequestBody @Valid req: SignUpRequest): ResponseEntity<Void> {
        userService.createUser(req.loginId, req.password)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/sign-in")
    suspend fun signIn(@RequestBody req: SignInRequest): ResponseEntity<SignInResponse> {
        val (loginId, password) = req
        val user = userService.getUser(loginId, password)
        val accessToken = jwtTokenProvider.generateAccessToken(user.id.toString(), listOf(user.role.name))
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id.toString())
        return ResponseEntity.ok(SignInResponse(accessToken, refreshToken, user))
    }
}
