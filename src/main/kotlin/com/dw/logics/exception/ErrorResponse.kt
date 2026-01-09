package com.dw.logics.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val message: String,
    val path: String? = null
) {
    companion object {
        fun of(errorCode: ErrorCode, path: String? = null): ErrorResponse {
            return ErrorResponse(
                status = errorCode.status.value(),
                message = errorCode.message,
                path = path
            )
        }

        fun of(errorCode: ErrorCode, message: String, path: String? = null): ErrorResponse {
            return ErrorResponse(
                status = errorCode.status.value(),
                message = message,
                path = path
            )
        }
    }
}

