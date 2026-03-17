package com.dw.logics.service

import com.dw.logics.config.DeliveryPartnerProperties
import com.dw.logics.domain.DeliveryTrackingLookupResult
import com.dw.logics.exception.UnsupportedDeliveryPartnerException
import com.dw.logics.external.ConfiguredPartnerDeliveryClient
import com.dw.logics.external.PartnerDeliveryClient
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.Locale

@Service
class DeliveryService(
    private val deliveryPartnerProperties: DeliveryPartnerProperties,
    private val webClientBuilder: WebClient.Builder
) {

    private lateinit var clients: Map<String, PartnerDeliveryClient>

    @PostConstruct
    fun initializeClients() {
        clients = deliveryPartnerProperties.partners.mapKeys { (partnerCode, _) -> normalize(partnerCode) }
            .mapValues { (partnerCode, properties) ->
                ConfiguredPartnerDeliveryClient(partnerCode, properties, webClientBuilder)
            }
    }

    suspend fun lookup(partnerCode: String, laasInvoiceNoList: List<String>): DeliveryTrackingLookupResult {
        val normalizedPartnerCode = normalize(partnerCode)
        val client = clients[normalizedPartnerCode] ?: throw UnsupportedDeliveryPartnerException(partnerCode)

        return DeliveryTrackingLookupResult(
            partnerCode = client.partnerCode,
            deliveries = client.lookup(laasInvoiceNoList)
        )
    }

    private fun normalize(value: String): String = value.trim().lowercase(Locale.getDefault())
}
