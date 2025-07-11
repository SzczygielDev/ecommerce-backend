package pl.szczygieldev.order.infrastructure.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class PaymentNotificationFilter(
    @Value("\${app.externalApi.psp.notification.token}")
    private final val notificationToken: String,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        
        if (request.requestURI.startsWith("/payments/notification")) {
            val apiKey = request.getHeader("X-API-KEY")
            if (apiKey == null || apiKey != notificationToken) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Invalid notification token")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}