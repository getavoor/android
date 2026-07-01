package app.avoor.planbot.data.auth.social

/**
 * Performs social authentication.
 */
interface SocialAuthProvider {
    /**
     * Perform automatic login.
     *
     * Automatic login is an action that transparently authorizes the
     * user without visible UI.
     */
    suspend fun automaticLogin()

    /**
     * Perform login.
     */
    suspend fun performLogin()

    /**
     * Sign out of an account.
     *
     * After this method is called, the social auth provider should act
     * as if the app has just been installed.
     */
    suspend fun signOut()
}