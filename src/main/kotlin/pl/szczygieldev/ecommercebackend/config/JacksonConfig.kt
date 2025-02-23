package pl.szczygieldev.ecommercebackend.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean

@Configuration
class JacksonConfig {

    @Bean
    fun jackson(): ObjectMapper {
        val kotlinModule = KotlinModule.Builder()
            .enable(KotlinFeature.SingletonSupport)
            .build()

        return JsonMapper.builder()
            .addModule(kotlinModule)
            .addModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()

    }
}