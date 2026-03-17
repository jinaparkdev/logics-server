package com.dw.logics.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "delivery")
data class DeliveryPartnerProperties(
    val partners: Map<String, DeliveryPartnerClientProperties> = emptyMap()
)

data class DeliveryPartnerClientProperties(
    val baseUrl: String,
    val deliveryLookupPath: String,
    val apiKeyHeaderName: String? = null,
    val apiKey: String? = null
)
