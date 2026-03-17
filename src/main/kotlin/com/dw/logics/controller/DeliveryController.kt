package com.dw.logics.controller

import com.dw.logics.domain.DeliveryTrackingLookupResult
import com.dw.logics.service.DeliveryService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class DeliveryTrackingLookupRequest(
    @field:NotBlank(message = "협력사 코드는 필수입니다")
    val partnerCode: String,

    @field:Size(min = 1, max = 50, message = "운송장 번호는 1건 이상 50건 이하로 조회해야 합니다")
    val laasInvoiceNoList: List<@NotBlank(message = "운송장 번호는 비어 있을 수 없습니다") String>
)

@RestController
@RequestMapping("/api/deliveries")
class DeliveryTrackingController(
    private val deliveryService: DeliveryService
) {

    @PostMapping("/lookup")
    suspend fun lookup(
        @RequestBody @Valid request: DeliveryTrackingLookupRequest
    ): ResponseEntity<DeliveryTrackingLookupResult> = ResponseEntity.ok(
        deliveryService.lookup(request.partnerCode, request.laasInvoiceNoList)
    )
}
