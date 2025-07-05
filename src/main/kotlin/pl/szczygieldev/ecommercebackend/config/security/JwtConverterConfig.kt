package pl.szczygieldev.ecommercebackend.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

@Configuration
class JwtConverterConfig {

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()

        converter.setJwtGrantedAuthoritiesConverter { jwt: Jwt ->
            val authorities = mutableListOf<GrantedAuthority>()

            val realmRoles: List<*> = (jwt.getClaimAsMap("realm_access")?.get("roles") as? List<*>) ?: emptyList<Any>()
            val stringRealmRoles = realmRoles
                .filterIsInstance<String>()
                .map { it.lowercase() }
            enumValues<Role>().forEach { role ->
                if (stringRealmRoles.contains(role.toString().lowercase())) {
                    authorities += SimpleGrantedAuthority("ROLE_$role")
                }

            }

            authorities
        }

        return converter
    }
}