package org.example.auctioncommon.auth.service

import org.example.auctioncommon.global.error.CustomException
import org.example.auctioncommon.global.error.GlobalErrorCode
import org.springframework.stereotype.Service

@Service
class AuthProcessor {

    fun validateRedisRefreshToken(redisRefreshToken: String?, refreshToken: String){
        if (redisRefreshToken != refreshToken) {
            throw CustomException(GlobalErrorCode.INVALID_TOKEN)
        }
    }

}