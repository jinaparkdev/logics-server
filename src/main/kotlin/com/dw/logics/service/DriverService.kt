package com.dw.logics.service

import com.dw.logics.controller.CreateDriverRequest
import com.dw.logics.domain.Driver
import com.dw.logics.entity.DriverEntity
import com.dw.logics.utils.dbQuery
import org.springframework.stereotype.Service

@Service
class DriverService {

    suspend fun createDriver(request: CreateDriverRequest): Driver = dbQuery {
        DriverEntity.new {
            this.name = request.name
            this.phoneNumber = request.phoneNumber
            this.vehicleNumber = request.vehicleNumber
        }.toModel()
    }

    suspend fun getDriverById(id: Long): Driver? = dbQuery {
        DriverEntity.findById(id)?.toModel()
    }
}

