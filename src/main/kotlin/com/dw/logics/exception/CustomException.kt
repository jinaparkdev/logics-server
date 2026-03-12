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

class ExternalApiCallException(
    val statusCode: Int,
    responseBody: String
) : CustomException(
    ErrorCode.EXTERNAL_API_ERROR,
    "외부 API가 실패했습니다. status=$statusCode body=${responseBody.take(300)}"
)

class ExternalApiCommunicationException(
    endpoint: String,
    cause: Throwable
) : CustomException(
    ErrorCode.EXTERNAL_API_ERROR,
    "외부 API 통신에 실패했습니다. endpoint=$endpoint cause=${cause.message}"
)

class InvalidExternalApiEndpointException(
    endpoint: String
) : CustomException(
    ErrorCode.INVALID_INPUT,
    "허용되지 않은 외부 API endpoint 입니다. endpoint=${endpoint.take(300)}"
)
