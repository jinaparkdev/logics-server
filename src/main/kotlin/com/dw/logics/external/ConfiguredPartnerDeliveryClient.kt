package com.dw.logics.external

import com.dw.logics.config.DeliveryPartnerClientProperties
import com.dw.logics.domain.DeliveryTrackingInfo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.WebClient

class ConfiguredPartnerDeliveryClient(
    override val partnerCode: String,
    private val properties: DeliveryPartnerClientProperties,
    webClientBuilder: WebClient.Builder
) : AbstractExternalWebClient(webClientBuilder, properties.baseUrl), PartnerDeliveryClient {

    private val objectMapper = jacksonObjectMapper()

    override suspend fun lookup(laasInvoiceNoList: List<String>): List<DeliveryTrackingInfo> =
        execute(
            ExternalApiRequest(
                method = HttpMethod.POST,
                endpoint = properties.deliveryLookupPath,
                headers = mapOf("Content-Type" to "application/json"),
                body = PartnerDeliveryTrackingLookupRequest(laasInvoiceNoList)
            )
        ) { raw ->
            raw.requireSuccess()
                .readBody<List<PartnerDeliveryTrackingResponse>>(objectMapper)
                .map { it.toDomain() }
        }
}