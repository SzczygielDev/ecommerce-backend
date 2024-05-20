package pl.szczygieldev.ecommercebackend.infrastructure.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean

@Configuration
class JacksonConfig {

    @Bean
    fun jackson(): ObjectMapper = ObjectMapper().registerKotlinModule()
}