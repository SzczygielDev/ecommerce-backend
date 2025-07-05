package pl.szczygieldev.ecommercebackend.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .addSecurityItem(SecurityRequirement().addList("keycloak"))
            .components(
                io.swagger.v3.oas.models.Components()
                    .addSecuritySchemes(
                        "keycloak", SecurityScheme()
                            .type(SecurityScheme.Type.OAUTH2)
                            .flows(
                                OAuthFlows().authorizationCode(
                                    OAuthFlow()
                                        .authorizationUrl("http://localhost:8085/realms/ecommerce/protocol/openid-connect/auth")
                                        .tokenUrl("http://localhost:8085/realms/ecommerce/protocol/openid-connect/token")
                                        .scopes(
                                            Scopes().apply {
                                                addString("openid", "OpenID Connect scope")
                                                addString("profile", "Access profile information")
                                            }
                                        )
                                )
                            )
                    ) .addSecuritySchemes(
                        "bearerAuth", SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            ).addSecurityItem(
                SecurityRequirement()
                    .addList("keycloak")
                    .addList("bearerAuth")
            )
    }
}