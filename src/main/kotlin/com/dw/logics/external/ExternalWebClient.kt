package com.dw.logics.external

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ExternalWebClient(
    webClientBuilder: WebClient.Builder
) : AbstractExternalWebClient(webClientBuilder)
