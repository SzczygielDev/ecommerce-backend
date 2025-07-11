package pl.szczygieldev.external.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiKeyFilterConfig(@Value("\${app.externalApi.key}")
                         private final val externalApiKey: String,): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if(request.requestURI.startsWith("/external/psp/public")){
            filterChain.doFilter(request, response)
            return
        }

        if (request.requestURI.startsWith("/external/")) {
            val apiKey = request.getHeader("X-API-KEY")
            if (apiKey == null || apiKey != externalApiKey) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Invalid API Key")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}