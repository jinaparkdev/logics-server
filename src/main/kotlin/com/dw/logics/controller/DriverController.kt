package com.dw.logics.controller

import com.dw.logics.domain.Driver
import com.dw.logics.service.DriverService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


data class CreateDriverRequest(
    val name: String,
    val phoneNumber: String,
    val vehicleNumber: String
)

@RestController
@RequestMapping("/api/driver")
class DriverController(private val driverService: DriverService) {

    @PostMapping
    suspend fun createDriver(@RequestBody request: CreateDriverRequest): ResponseEntity<Driver> {
        val driver = driverService.createDriver(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(driver)
    }

    @GetMapping("/{id}")
    suspend fun getDriverById(@PathVariable id: Long): ResponseEntity<Driver> {
        val driver = driverService.getDriverById(id)
        return if (driver != null) {
            ResponseEntity.ok(driver)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

