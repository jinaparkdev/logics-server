package com.dw.logics.external

import com.dw.logics.config.DeliveryPartnerClientProperties
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

class ConfiguredPartnerDeliveryClientTest {

    private lateinit var mockWebServer: MockWebServer

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
    fun `configured partner domain and path are used for delivery lookup`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    [
                      {
                        "companyId": 10,
                        "deliveryOrderId": 20,
                        "laasInvoiceNo": "INV-1",
                        "shipperCode": "CJ",
                        "shipperName": "대한통운",
                        "addressRefineResult": "S",
                        "addressRefineMessage": "OK",
                        "addressRefineDeliveryLocationCode": "TC01",
                        "addressRefineDeliveryLocationName": "강남TC",
                        "addressRefineDeliveryZoneCode": "Z01",
                        "addressRefineDeliveryZoneName": "강남1",
                        "addressRefineStreet": "서울시 강남구 테헤란로",
                        "addressRefineAdministrative": "역삼동",
                        "addressRefineDeliveryBranchCode": "B01",
                        "addressRefineDeliveryBranchName": "강남지점",
                        "customerOrderNo": "ORDER-1",
                        "customerReferenceNo": "REF-1",
                        "requestType": "R",
                        "orderType": "10",
                        "serviceTypeCode": "DAY",
                        "serviceTypeName": "당일배송",
                        "boxTypeCode": "B10",
                        "boxTypeName": "소형",
                        "senderWarehouseName": "메인센터",
                        "senderName": "보내는분",
                        "senderPhoneNo": "02-1111-2222",
                        "senderMobileNo": "010-1111-2222",
                        "senderZipNo": "12345",
                        "senderAddress": "서울시 송파구",
                        "senderAddressDetail": "101동",
                        "receiverName": "받는분",
                        "receiverPhoneNo": "02-3333-4444",
                        "receiverMobileNo": "010-3333-4444",
                        "receiverZipNo": "54321",
                        "receiverAddress": "서울시 강남구",
                        "receiverAddressDetail": "202호",
                        "deliveryMemo": "문앞",
                        "deliveryNotice": "부재 시 연락",
                        "goodsInformation": "식품",
                        "goodsAmountInformation": "25000",
                        "goodsCode1": "G1",
                        "goodsName1": "사과",
                        "goodsQty1": "1",
                        "goodsAmount1": "25000",
                        "goodsCode2": null,
                        "goodsName2": null,
                        "goodsQty2": null,
                        "goodsAmount2": null,
                        "goodsCode3": null,
                        "goodsName3": null,
                        "goodsQty3": null,
                        "goodsAmount3": null,
                        "goodsCode4": null,
                        "goodsName4": null,
                        "goodsQty4": null,
                        "goodsAmount4": null,
                        "goodsCode5": null,
                        "goodsName5": null,
                        "goodsQty5": null,
                        "goodsAmount5": null
                      }
                    ]
                    """.trimIndent()
                )
        )

        val client = ConfiguredPartnerDeliveryClient(
            partnerCode = "sample",
            properties = DeliveryPartnerClientProperties(
                baseUrl = mockWebServer.url("/").toString(),
                deliveryLookupPath = "/api/v1/deliveries/lookup"
            ),
            webClientBuilder = WebClient.builder()
        )

        val response = client.lookup(listOf("INV-1"))
        val recordedRequest = mockWebServer.takeRequest()

        Assertions.assertEquals("/api/v1/deliveries/lookup", recordedRequest.path)
        Assertions.assertEquals("POST", recordedRequest.method)
        Assertions.assertEquals("""{"laasInvoiceNoList":["INV-1"]}""", recordedRequest.body.readUtf8())
        Assertions.assertEquals(1, response.size)
        Assertions.assertEquals("INV-1", response.first().laasInvoiceNo)
        Assertions.assertEquals("대한통운", response.first().shipperName)
    }
}