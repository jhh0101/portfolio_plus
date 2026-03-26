interface RefreshTokenRepository {
    fun saveRefreshTokenBidirectional(userId: Long, token: String)
    fun getUserIdByToken(token: String): Long?
    fun deleteRefreshToken(userId: Long)
    fun redisRefreshToken(userId: Long): String?
}