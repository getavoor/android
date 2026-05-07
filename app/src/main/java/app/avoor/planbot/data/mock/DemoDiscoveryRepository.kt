package app.avoor.planbot.data.mock

import app.avoor.planbot.data.discovery.DiscoveryCompatCheckResult
import app.avoor.planbot.data.discovery.DiscoveryFailedException
import app.avoor.planbot.data.discovery.DiscoveryRepository
import app.avoor.planbot.data.discovery.DiscoveryResponse
import app.avoor.planbot.data.discovery.DiscoveryStartBody
import app.avoor.planbot.data.discovery.DiscoveryStatusCodes
import app.avoor.planbot.data.discovery.DiscoveryUser
import app.avoor.planbot.data.discovery.GroupDiscoverySession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DemoDiscoveryRepository: DiscoveryRepository {
    var emit: Boolean = false

    override suspend fun logIn(accessToken: String): Int {
        return 200
    }

    override suspend fun compatCheck(): DiscoveryCompatCheckResult {
        return DiscoveryCompatCheckResult(
            compatible = true,
            protoVer = 0
        )
    }

    override fun getEvents(): Flow<DiscoveryResponse> = flow {
    }

    override suspend fun start(body: DiscoveryStartBody) {
        // Start emitting
        emit = true
    }

    override suspend fun continueDiscovery() {
        if (!emit) emit = true
    }

    override suspend fun <T> sendRawData(cmd: String, arg: T?): Int {
        return DiscoveryStatusCodes.OK
    }

    override fun disconnect() {
        // Stop emitting to signify that the repo is now disconnected
        emit = false
    }

    override fun connect() {

    }

    override suspend fun approve(user: DiscoveryUser) {

    }

    override suspend fun join(id: String) {
        
    }

    override suspend fun leave() {

    }

    override suspend fun getGroup(): GroupDiscoverySession {
        // INVALID_DATA means that the user isn't in a group, and the demo
        // repository doesn't support groups (at least for now)
        throw DiscoveryFailedException(DiscoveryStatusCodes.INVALID_DATA)
    }

    override fun isConnected(): Boolean {
        return true
    }
}