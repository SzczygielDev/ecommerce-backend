package pl.szczygieldev.ecommercebackend.config
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig(
    private val jwtAuthenticationConverter: JwtAuthenticationConverter,

) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf {
                ignoringRequestMatchers(PathRequest.toH2Console())
                ignoringRequestMatchers("/external/psp/**","/payments/**")
            }
            authorizeHttpRequests {
                authorize("/public/**", permitAll)
                authorize("/images/**", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize(PathRequest.toH2Console(), permitAll)
                authorize("/external/**", permitAll)
                authorize("/payments/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer {
                jwt {
                    jwtAuthenticationConverter = this@SecurityConfig.jwtAuthenticationConverter
                }
            }
            headers {
                frameOptions {
                    disable()
                }
            }

        }
        return http.build()
    }
    @Bean
    fun jwtDecoder(): JwtDecoder = JwtDecoders.fromIssuerLocation("http://localhost:8085/realms/ecommerce")
}