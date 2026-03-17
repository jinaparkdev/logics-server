package com.dw.logics.external

import com.dw.logics.domain.DeliveryTrackingInfo

interface PartnerDeliveryClient {
    val partnerCode: String

    suspend fun lookup(laasInvoiceNoList: List<String>): List<DeliveryTrackingInfo>
}
