package org.example.auctioncommon.global.auth.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.auctioncommon.global.auth.service.JwtService
import org.example.auctioncommon.global.error.CustomException
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder.getContext
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(javaClass)

    @Throws(ServletException::class, IOException::class)
    protected override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = getTokenFromRequest(request)

            if (StringUtils.hasText(token)) {
                if (jwtService.validateToken(token)) {
                    val userId: Long = jwtService.getUserIdFromToken(token)
                    val email: String = jwtService.getEmailFromToken(token)
                    val role: String = jwtService.getRoleFromToken(token)
                    val nickname: String = jwtService.getNicknameFromToken(token)

                    val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))

                    val authentication = UsernamePasswordAuthenticationToken(email, null, authorities)
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                    getContext().authentication = authentication

                    log.debug(
                        "Set authentication for user: userId={}, email={}, nickname={} role={}",
                        userId,
                        email,
                        nickname,
                        role
                    )
                }
            }
        } catch (e: CustomException) {
            log.error("JWT authentication error: {}", e.message)
        }

        filterChain.doFilter(request, response)
    }

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken: String? = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }
}
