package com.dw.logics.exception

open class CustomException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : RuntimeException(message)


// User
class InvalidCredentialsException(message: String = ErrorCode.INVALID_CREDENTIALS.message) :
    CustomException(ErrorCode.INVALID_CREDENTIALS, message)

class DuplicateCredentialException(message: String = ErrorCode.DUPLICATE_CREDENTIAL.message) :
    CustomException(ErrorCode.DUPLICATE_CREDENTIAL, message)

class UserNotFoundException(message: String = ErrorCode.USER_NOT_FOUND.message) :
    CustomException(ErrorCode.USER_NOT_FOUND, message)
