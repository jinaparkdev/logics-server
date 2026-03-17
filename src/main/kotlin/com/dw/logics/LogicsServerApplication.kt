package com.dw.logics

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class LogicsServerApplication

fun main(args: Array<String>) {
    runApplication<LogicsServerApplication>(*args)
}
