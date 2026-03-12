package com.dw.logics.external

import com.dw.logics.exception.ExternalApiCallException
import com.dw.logics.exception.InvalidExternalApiEndpointException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.WebClient
import kotlin.test.assertFailsWith

class ExternalWebClientTest {

    private lateinit var mockWebServer: MockWebServer
    private val objectMapper = jacksonObjectMapper()
    private val client = ExternalWebClient(WebClient.builder())

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `headers endpoint and response mapping can be customized by caller`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"id":7,"name":"alpha"}""")
        )

        val request = ExternalApiRequest(
            method = HttpMethod.POST,
            endpoint = mockWebServer.url("/vendors").toString(),
            queryParams = mapOf("status" to listOf("active")),
            headers = mapOf(
                "Authorization" to "Bearer sample-token",
                "X-Trace-Id" to "trace-123"
            ),
            body = mapOf("name" to "request-body")
        )

        val response = client.execute(request) { raw ->
            raw.requireSuccess().readBody<ExternalVendorResponse>(objectMapper)
        }

        val recordedRequest = mockWebServer.takeRequest()

        assertEquals("/vendors?status=active", recordedRequest.path)
        assertEquals("Bearer sample-token", recordedRequest.getHeader("Authorization"))
        assertEquals("trace-123", recordedRequest.getHeader("X-Trace-Id"))
        assertEquals("""{"name":"request-body"}""", recordedRequest.body.readUtf8())
        assertEquals(7, response.id)
        assertEquals("alpha", response.name)
    }

    @Test
    fun `non successful response can be rejected by caller mapping`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(502)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"message":"upstream failed"}""")
        )

        val request = ExternalApiRequest(
            endpoint = mockWebServer.url("/vendors").toString()
        )

        assertFailsWith<ExternalApiCallException> {
            client.execute(request) { raw ->
                raw.requireSuccess()
            }
        }
    }

    @Test
    fun `only absolute http endpoints are allowed`() = runTest {
        val request = ExternalApiRequest(
            endpoint = "javascript:alert(1)"
        )

        assertFailsWith<InvalidExternalApiEndpointException> {
            client.execute(request)
        }
    }

    @Test
    fun `subclass can set baseUrl and use relative endpoints`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"id":9,"name":"beta"}""")
        )

        val baseUrlClient = TestBaseUrlExternalWebClient(
            WebClient.builder(),
            mockWebServer.url("/").toString()
        )

        val response = baseUrlClient.execute(
            ExternalApiRequest(
                endpoint = "/vendors/9",
                queryParams = mapOf("include" to listOf("profile"))
            )
        ) { raw ->
            raw.requireSuccess().readBody<ExternalVendorResponse>(objectMapper)
        }

        val recordedRequest = mockWebServer.takeRequest()

        assertEquals("/vendors/9?include=profile", recordedRequest.path)
        assertEquals(9, response.id)
        assertEquals("beta", response.name)
    }

    data class ExternalVendorResponse(
        val id: Long,
        val name: String
    )

    private class TestBaseUrlExternalWebClient(
        webClientBuilder: WebClient.Builder,
        baseUrl: String
    ) : AbstractExternalWebClient(webClientBuilder, baseUrl)
}
