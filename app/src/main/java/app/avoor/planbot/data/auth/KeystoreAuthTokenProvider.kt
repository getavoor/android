package app.avoor.planbot.data.auth

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import app.avoor.planbot.data.prefs.PreferenceStore
import java.security.KeyStore
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class KeystoreAuthTokenProvider(
    private val preferenceStore: PreferenceStore
): AuthTokenProvider {

    private var secretKey: SecretKey? = null

    private suspend fun setIV(iv: ByteArray) {
        val encIV = Base64.encode(iv, Base64.NO_PADDING).toString(Charsets.UTF_8)
        preferenceStore.setIV(encIV)
    }

    private suspend fun getIV(): ByteArray? {
        // Attempt to get the saved IV, and if there is none, return null
        Log.d("avr#um", "Retrieving saved IV")
        val encIV = preferenceStore.getIV() ?: return null
        return Base64.decode(encIV, Base64.NO_PADDING)
    }

    private fun getKey(): SecretKey? {
        Log.d("avr#um", "Retrieving key")
        // Retrieve the secret key from the Keystore
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val secretKeyEntry = keyStore.getEntry("GmfdKey", null) ?: return null
        return (secretKeyEntry as KeyStore.SecretKeyEntry).secretKey
    }

    private fun generateKey(): SecretKey {
        Log.d("avr#um", "Generating key")
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec =
            KeyGenParameterSpec.Builder(
                "GmfdKey",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false) // Adjust as needed
                .setKeySize(256)
                .build()
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    private fun getOrGenerateKey(): SecretKey {
        return getKey() ?: generateKey()
    }

    init {
        secretKey = getOrGenerateKey()
    }

    // the access token can be stored in this class for short time use
    private var accessToken: String? = null

    override suspend fun getAccessToken(): String? {
        return accessToken
    }

    private val refreshTokenCryptoTransform = "AES/GCM/NoPadding"

    override suspend fun getRefreshToken(): String? {
        Log.d("avr#um", "Retrieving and decrypting refresh token")
        val cipher = Cipher.getInstance(refreshTokenCryptoTransform)
        // Get the saved IV
        val iv = getIV()
            // If it is null, alert about it
            ?: throw NullPointerException("Saved IV is null, has an auth token been encrypted yet?")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        val encodedToken = preferenceStore.getRefreshToken() ?: return null
        val encryptedToken = Base64.decode(encodedToken, Base64.NO_PADDING)
        return safeDecode(cipher, encryptedToken)
    }

    private fun safeDecode(cipher: Cipher, encryptedToken: ByteArray?): String? {
        try {
            val decode = cipher.doFinal(encryptedToken).toString(Charsets.UTF_8)
            return decode
        } catch(e: AEADBadTagException) {
            Log.d("avr#katp", "key is invalid")
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            if (keyStore.containsAlias("GmfdKey")) {
                keyStore.deleteEntry("GmfdKey")
            }
            return null
        }
    }

    override suspend fun setAccessToken(token: String) {
        accessToken = token
    }

    override suspend fun setRefreshToken(token: String) {
        val cipher = Cipher.getInstance(refreshTokenCryptoTransform)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val ivParams = cipher.parameters.getParameterSpec(GCMParameterSpec::class.java)
        setIV(ivParams.iv)
        val encryptedToken = cipher.doFinal(token.toByteArray(Charsets.UTF_8))
        val encodedToken = Base64.encode(encryptedToken, Base64.NO_PADDING).toString(Charsets.UTF_8)
        preferenceStore.setRefreshToken(encodedToken)
    }

    override suspend fun deleteAccessToken() {
        accessToken = null
    }

    override suspend fun deleteRefreshToken() {
        preferenceStore.deleteRefreshToken()
    }
}