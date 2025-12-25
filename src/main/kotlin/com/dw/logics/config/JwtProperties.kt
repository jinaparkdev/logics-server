package com.dw.logics.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "jwt")
@Configuration
data class JwtProperties(
    var issuer: String = "logics-server",
    var refreshTokenExpiration: Long = 604800000,
    var accessTokenExpiration: Long = 3600000,
    var secret: String = "",
)