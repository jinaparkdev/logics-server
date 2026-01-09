package com.dw.logics.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(
        ex: CustomException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> = createErrorResponse(ex.errorCode, ex.message, request)


    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.first().defaultMessage
        log.error("유효성 검사 중 예외가 발생했습니다")
        return createErrorResponse(ErrorCode.INVALID_INPUT, message = errors, request = request)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error("예상치 못한 예외가 발생했습니다: ${ex.message}", ex)
        return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, request = request)
    }

    private fun createErrorResponse(
        errorCode: ErrorCode,
        message: String? = errorCode.message,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse.of(
            errorCode = errorCode,
            message = message ?: errorCode.message,
            path = request.requestURI
        )
        log.error("예외가 발생했습니다: $errorResponse")
        return ResponseEntity.status(errorCode.status).body(errorResponse)
    }
}