package com.dw.logics.external

import com.dw.logics.exception.ExternalApiCommunicationException
import com.dw.logics.exception.InvalidExternalApiEndpointException
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpHeaders
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.net.URISyntaxException

abstract class AbstractExternalWebClient(
    webClientBuilder: WebClient.Builder,
    baseUrl: String? = null
) : ExternalApiClient {

    private val configuredBaseUri: URI? = baseUrl?.let { validateAbsoluteUri(it) }
    private val webClient: WebClient = configuredBaseUri
        ?.let { webClientBuilder.clone().baseUrl(it.toString()).build() }
        ?: webClientBuilder.build()

    override suspend fun execute(request: ExternalApiRequest): ExternalApiRawResponse {
        val uri = createUri(request)

        return try {
            val requestSpec = webClient
                .method(request.method)
                .uri(uri)
                .headers { headers -> headers.setAll(request.headers) }
            val responseSpec = if (request.body != null) {
                requestSpec.bodyValue(request.body)
            } else {
                requestSpec
            }

            responseSpec
                .exchangeToMono { clientResponse ->
                    clientResponse.bodyToMono(String::class.java)
                        .defaultIfEmpty("")
                        .map { body ->
                            ExternalApiRawResponse(
                                statusCode = clientResponse.statusCode(),
                                headers = HttpHeaders(clientResponse.headers().asHttpHeaders()),
                                body = body
                            )
                        }
                }
                .awaitSingle()
        } catch (ex: WebClientRequestException) {
            throw ExternalApiCommunicationException(uri.toString(), ex)
        }
    }

    private fun createUri(request: ExternalApiRequest): URI {
        val resolvedUri = resolveEndpoint(request.endpoint)
        val queryParams = LinkedMultiValueMap<String, String>()
        request.queryParams.forEach { (key, values) -> queryParams[key] = values.toMutableList() }

        return UriComponentsBuilder
            .fromUri(resolvedUri)
            .queryParams(queryParams)
            .build(true)
            .toUri()
    }

    private fun resolveEndpoint(endpoint: String): URI {
        val uri = parseUri(endpoint)

        return when {
            uri.isAbsolute -> validateAbsoluteUri(endpoint)
            configuredBaseUri != null -> configuredBaseUri.resolve(uri)
            else -> throw InvalidExternalApiEndpointException(endpoint)
        }
    }

    private fun validateAbsoluteUri(value: String): URI {
        val uri = parseUri(value)
        val scheme = uri.scheme?.lowercase()

        if (!uri.isAbsolute || uri.host.isNullOrBlank() || scheme !in SUPPORTED_SCHEMES || uri.userInfo != null) {
            throw InvalidExternalApiEndpointException(value)
        }

        return uri
    }

    private fun parseUri(value: String): URI = try {
        URI(value)
    } catch (_: URISyntaxException) {
        throw InvalidExternalApiEndpointException(value)
    }

    companion object {
        private val SUPPORTED_SCHEMES = setOf("http", "https")
    }
}
