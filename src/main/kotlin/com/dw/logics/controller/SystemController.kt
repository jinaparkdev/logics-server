package com.dw.logics.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/system")
class SystemController {
    @GetMapping("/health")
    suspend fun healthCheck() = "OK"
}