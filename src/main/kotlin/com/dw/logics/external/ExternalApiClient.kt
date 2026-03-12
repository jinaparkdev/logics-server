package com.dw.logics.external

import com.dw.logics.exception.ExternalApiCallException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode

data class ExternalApiRequest(
    val method: HttpMethod = HttpMethod.GET,
    val endpoint: String,
    val queryParams: Map<String, List<String>> = emptyMap(),
    val headers: Map<String, String> = emptyMap(),
    val body: Any? = null
)

data class ExternalApiRawResponse(
    val statusCode: HttpStatusCode,
    val headers: HttpHeaders,
    val body: String
) {
    fun requireSuccess(): ExternalApiRawResponse {
        if (!statusCode.is2xxSuccessful) {
            throw ExternalApiCallException(statusCode.value(), body)
        }
        return this
    }

    inline fun <reified T> readBody(objectMapper: ObjectMapper): T =
        objectMapper.readValue(body, object : TypeReference<T>() {})
}

interface ExternalApiClient {
    suspend fun execute(request: ExternalApiRequest): ExternalApiRawResponse

    suspend fun <T> execute(
        request: ExternalApiRequest,
        responseMapper: (ExternalApiRawResponse) -> T
    ): T = responseMapper(execute(request))
}
