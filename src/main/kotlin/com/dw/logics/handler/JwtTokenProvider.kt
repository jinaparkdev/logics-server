package com.dw.logics.handler

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {

    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    fun generateAccessToken(userId: String, roles: List<String> = emptyList()): String {
        return generateToken(userId, roles, jwtProperties.accessTokenExpiration)
    }

    fun generateRefreshToken(userId: String): String {
        return generateToken(userId, emptyList(), jwtProperties.refreshTokenExpiration)
    }

    private fun generateToken(userId: String, roles: List<String>, expiration: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        return Jwts.builder()
            .subject(userId)
            .claim("roles", roles)
            .issuer(jwtProperties.issuer)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }

    fun getUserIdFromToken(token: String): String {
        return getClaims(token).subject
    }

    fun getRolesFromToken(token: String): List<String> {
        val claims = getClaims(token)
        @Suppress("UNCHECKED_CAST")
        return claims["roles"] as? List<String> ?: emptyList()
    }

    fun validateToken(token: String): Boolean {
        return try {
            getClaims(token)
            true
        } catch (e: Exception) {
            logger.info("토큰 검증 중 예외 발생:{}", { e.printStackTrace() })
            false
        }
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun getExpirationFromToken(token: String): Date {
        return getClaims(token).expiration
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            getExpirationFromToken(token).before(Date())
        } catch (e: Exception) {
            logger.info("토큰 만료 체크 중 예외 발생:{}", { e.printStackTrace() })
            true
        }
    }
}

@ConfigurationProperties(prefix = "jwt")
@Configuration
data class JwtProperties(
    var issuer: String = "logics-server",
    var refreshTokenExpiration: Long = 604800000,
    var accessTokenExpiration: Long = 3600000,
    var secret: String = "",
)