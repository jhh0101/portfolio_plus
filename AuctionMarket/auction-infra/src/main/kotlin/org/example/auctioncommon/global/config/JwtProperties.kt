package org.example.auctioncommon.global.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
class JwtProperties(
    val secret: String? = null,
    val accessTokenExpiration: Long? = null,
    val refreshTokenExpiration: Long? = null
)
