package app.avoor.planbot.data.discovery

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import tech.cataspect.socketx.Socket

class SocketDiscoveryRepository(
    private val ioSocket: Socket
): DiscoveryRepository {

    /**
     * Does a basic request and throws an exception if the request failed.
     *
     * @param body the body of the request.
     */
    private suspend fun doRequest(body: JSONObject) {
        val args = ioSocket.emitAndWait("dsc", body)
        // check if the operation was successful (it should be)
        val statusCode = args[0] as Int
        if (statusCode != DiscoveryStatusCodes.OK) {
            throw DiscoveryFailedException(statusCode)
        }
    }

    override suspend fun logIn(accessToken: String): Int {
        Log.d("avr#sdr", "conencted: ${ioSocket.connected}")
        Log.d("avr#sdr", "doing login")
        val args = ioSocket.emitAndWait("dsc", JSONObject(
            mapOf("cmd" to "enter", "arg" to accessToken)
        ))
        Log.d("avr#sdr", "emit is done")
        return args[0] as Int
    }

    override suspend fun compatCheck(): DiscoveryCompatCheckResult {
        val args = ioSocket.emitAndWait("dsc", JSONObject(
            mapOf("cmd" to "compat", "arg" to PROTO_VER)
        )
        )
        // check if the operation was successful (it should be)
        val statusCode = args[0] as Int
        if (statusCode != DiscoveryStatusCodes.OK) {
            throw DiscoveryFailedException(statusCode)
        }
        // return the result
        return DiscoveryCompatCheckResult(
            compatible = args[2] as Boolean,
            protoVer = args[1] as Int
        )
    }

    override fun getEvents(): Flow<DiscoveryResponse> = ioSocket.on("dsc")
        .map {
            // Get the raw JSON object
            val jo = (it[0] as JSONObject)
            Log.d("avr#sdr",jo.toString())
            // Check the command
            if (jo.has("cmd")) {
                val cmd = jo["cmd"] as String
                Log.d("avr#sdr", cmd)
                when (cmd) {
                    // TODO [fluff] add commands
                    // Handle user-related commands with the same response object
                    DiscoveryCommand.JOIN.id -> {
                        DiscoveryResponse(
                            DiscoveryCommand.JOIN,
                            user = (jo["user"] as JSONObject).parseDiscoveryUser()
                        )
                    }
                    DiscoveryCommand.APPROVE.id -> {
                        DiscoveryResponse(
                            DiscoveryCommand.APPROVE,
                            user = (jo["user"] as JSONObject).parseDiscoveryUser()
                        )
                    }
                    DiscoveryCommand.LEAVE.id -> {
                        DiscoveryResponse(
                            DiscoveryCommand.LEAVE,
                            user = (jo["user"] as JSONObject).parseDiscoveryUser()
                        )
                    }
                    // If the command is DESTROY:
                    DiscoveryCommand.DESTROY.id -> {
                        DiscoveryResponse(DiscoveryCommand.DESTROY)
                    }
                    // parse other commands as UNKNOWN
                    else -> {
                        DiscoveryResponse(DiscoveryCommand.UNKNOWN)
                    }
                }
            }
            else {
                // Default to an unknown command
                DiscoveryResponse(DiscoveryCommand.UNKNOWN)
            }
        }

    override suspend fun start(body: DiscoveryStartBody) {
        doRequest(JSONObject(
            mapOf("cmd" to "start", "arg" to body.toJSON())
        ))
    }

    override suspend fun continueDiscovery() {
        doRequest(JSONObject(
            mapOf("cmd" to "cont")
        ))
    }

    override suspend fun <T> sendRawData(cmd: String, arg: T?): Int {
        val args = ioSocket.emitAndWait("dsc", JSONObject(
            mapOf("cmd" to cmd, "arg" to arg)
        ))
        return args[0] as Int
    }

    override fun disconnect() {
        ioSocket.disconnect()
    }

    override fun connect() {
        ioSocket.connect()
    }

    override suspend fun approve(user: DiscoveryUser) {
        doRequest(JSONObject(
            mapOf("cmd" to "approve", "arg" to user.id)
        ))
    }

    override suspend fun join(id: String) {
        doRequest(JSONObject(
            mapOf("cmd" to "join", "arg" to id)
        ))
    }

    override suspend fun leave() {
        doRequest(JSONObject(
            mapOf("cmd" to "leave")
        ))
    }

    override suspend fun getGroup(): GroupDiscoverySession {
        val args = ioSocket.emitAndWait("dsc", JSONObject(
            mapOf("cmd" to "group")
        ))
        // check if the operation was successful (it should be)
        val statusCode = args[0] as Int
        if (statusCode != DiscoveryStatusCodes.OK) {
            throw DiscoveryFailedException(statusCode)
        }
        // Get the second argument as a JSON object
        val groupJO = args[1] as JSONObject
        // Convert it to a group and return it
        return groupJO.parseDiscoveryGroup()
    }

    override fun isConnected(): Boolean {
        return ioSocket.connected
    }

    companion object {
        const val PROTO_VER = 8
    }
}