package com.dw.logics.service

import com.dw.logics.config.DeliveryPartnerClientProperties
import com.dw.logics.config.DeliveryPartnerProperties
import com.dw.logics.exception.UnsupportedDeliveryPartnerException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import kotlin.test.assertFailsWith

class DeliveryServiceTest {

    @Test
    fun `configured partner code is resolved case-insensitively`() = runTest {
        val service = DeliveryService(
            deliveryPartnerProperties = DeliveryPartnerProperties(
                partners = mapOf(
                    "partner-a" to DeliveryPartnerClientProperties(
                        baseUrl = "http://localhost:18080",
                        deliveryLookupPath = "/lookup"
                    )
                )
            ),
            webClientBuilder = WebClient.builder()
        )
        service.initializeClients()

        val exception = assertFailsWith<UnsupportedDeliveryPartnerException> {
            service.lookup("missing-partner", listOf("INV-1"))
        }

        assertEquals(
            "지원하지 않는 배송 협력사입니다. partnerCode=missing-partner",
            exception.message
        )
    }
}
