package com.dw.logics.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration("logicsDatabaseConfig")
class DatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    fun hikariConfig(): HikariConfig {
        return HikariConfig()
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    fun dataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @Primary
    fun dataSource(
        hikariConfig: HikariConfig,
        dataSourceProperties: DataSourceProperties
    ): DataSource {
        val url = dataSourceProperties.determineUrl()
        require(!url.isNullOrBlank()) { "정의되지 않은 spring.datasource.url" }

        hikariConfig.jdbcUrl = url
        hikariConfig.username = dataSourceProperties.determineUsername()
        hikariConfig.password = dataSourceProperties.determinePassword()
        hikariConfig.driverClassName = dataSourceProperties.determineDriverClassName()
        return HikariDataSource(hikariConfig)
    }
}
