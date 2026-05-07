package app.avoor.planbot.data.mock

import app.avoor.planbot.data.auth.AuthTokenProvider

class TemporaryAuthTokenProvider : AuthTokenProvider {

    var xt: String? = "DEMOasUserOneXT"
    var rt: String? = "DEMOasUserOneRT"

    override suspend fun getAccessToken(): String? {
        return xt
    }

    override suspend fun getRefreshToken(): String? {
        return rt
    }

    override suspend fun setAccessToken(token: String) {
        xt = token
    }

    override suspend fun setRefreshToken(token: String) {
        rt = token
    }

    override suspend fun deleteRefreshToken() {
        rt = null
    }

    override suspend fun deleteAccessToken() {
        xt = null
    }

}
