package app.avoor.planbot.data.auth

/**
 * Provides authorization tokens.
 */
interface AuthTokenProvider {
    /**
     * Gets the access token.
     */
    suspend fun getAccessToken(): String?

    /**
     * Gets the refresh token.
     */
    suspend fun getRefreshToken(): String?

    /**
     * Sets the access token.
     */
    suspend fun setAccessToken(token: String)

    /**
     * Sets the refresh token.
     */
    suspend fun setRefreshToken(token: String)

    /**
     * Returns the access token, or if it is null, the refresh token.
     *
     * If neither exists or both are null returns null.
     */
    suspend fun getAccessOrRefreshToken(): String? {
        // First try to get the access token
        val accessToken = getAccessToken()
        // If it exists, return it
        if (accessToken != null) return accessToken
        // Then try to get the refresh token
        try {
            // First try to get the access token
            val refreshToken = getRefreshToken()
            // If it exists, return it
            if (refreshToken != null) return refreshToken
        }
        // Catch the NullPointerException thrown when IV is null
        catch (e: NullPointerException) {
            // and return null
            return null
        }
        // In any other case return null for safety
        return null
    }

    /**
     * Deletes the refresh token.
     */
    suspend fun deleteRefreshToken()

    /**
     * Deletes the access token.
     */
    suspend fun deleteAccessToken()

    /**
     * Deletes the access and refresh tokens.
     */
    suspend fun deleteTokens() {
        deleteAccessToken()
        deleteRefreshToken()
    }
}

