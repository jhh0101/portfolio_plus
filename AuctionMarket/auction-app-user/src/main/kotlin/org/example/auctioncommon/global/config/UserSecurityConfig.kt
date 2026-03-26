package org.example.auctioncommon.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtService: JwtService,

    @field:Lazy
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {


    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors(withDefaults())
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",  // Swagger 문서 관련
                        "/api/auth/**", "/api/user/signup", "/api/user/verify",  // 로그인, 회원가입 관련
                        "/api/user/reset-password", "/api/groq/**"
                    ).permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/category").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/auction/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/order/*/auction").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/rating/**").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.addAllowedOriginPattern("*")  // 로컬/운영에 따라 조절
        configuration.addAllowedMethod("*")             // GET, POST, OPTIONS 등을 모두 허용
        configuration.addAllowedHeader("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager? {
        return configuration.authenticationManager
    }
}
