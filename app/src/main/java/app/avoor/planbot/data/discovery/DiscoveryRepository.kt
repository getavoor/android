package app.avoor.planbot.data.discovery

import kotlinx.coroutines.flow.Flow

/**
 * Repository for handling place discovery.
 */
interface DiscoveryRepository {
    /**
     * Sign in using the provided access token.
     *
     * @return the status code
     */
    suspend fun logIn(accessToken: String): Int

    /**
     * Perform a compatibility check.
     *
     * @return if this client is compatible
     */
    suspend fun compatCheck(): DiscoveryCompatCheckResult

    /**
     * Get the events returned by the server as a Flow.
     */
    fun getEvents(): Flow<DiscoveryResponse>

    /**
     * Start discovery.
     *
     * @param body parameters to start discovery with
     */
    suspend fun start(body: DiscoveryStartBody)

    /**
     * Continue discovery.
     */
    suspend fun continueDiscovery()

    /**
     * Send raw data to the underlying network protocol and return the response code.
     *
     * @param cmd the command to send.
     * @param arg the argument to send with the command, may be null.
     */
    suspend fun <T> sendRawData(cmd: String, arg: T? = null): Int

    /**
     * Disconnect from the discovery API.
     *
     * If the repository doesn't need to disconnect leave this empty.
     */
    fun disconnect()

    /**
     * Connect to the discovery API.
     *
     * If the repository doesn't need to connect leave this empty.
     */
    fun connect()

    /**
     * Approve a user that wants to join this group.
     * @param user the user to approve.
     */
    suspend fun approve(user: DiscoveryUser)

    /**
     * Join a discovery group.
     * @param id the ID of the desired group.
     */
    suspend fun join(id: String)

    /**
     * Leave the current discovery group.
     *
     * Requires the user to be in a discovery group.
     *
     * **If this user is the group's owner, the group will be deleted.**
     */
    suspend fun leave()

    /**
     * Get the user's current discovery group.
     *
     * Requires the user to be in a discovery group.
     */
    suspend fun getGroup(): GroupDiscoverySession

    /**
     * Is this socket connected?
     *
     * If the repository doesn't need to connect leave this empty.
     */
    fun isConnected(): Boolean
}

