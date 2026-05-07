package tech.cataspect.socketx

import android.util.Log
import io.socket.client.AckWithTimeout
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.URI
import kotlin.coroutines.resume

/**
 * A socket.io socket.
 */
class Socket (
    /**
     * The URI to connect to.
     */
    uri: String,
    /**
     * Options to set the socket up with.
     */
    options: IO.Options,
    /**
     * Enables extra logging.
     */
    private val debug: Boolean = false
) {

    /**
     * The base socket.io socket.
     */
    private val baseSocket: io.socket.client.Socket

    init {
        val baseUri = URI.create(uri)
        baseSocket = IO.socket(baseUri, options)
    }

    /**
     * Listen to the provided event.
     *
     * When a message for this event is received from the server, its arguments are emitted by the flow.
     *
     * Note that the arguments will be returned as an array of [Any] objects. It's recommended to convert
     * them to the type the server sends (using the `as` operator), either in collectors or by using
     * [Flow.map] on this flow.
     *
     * @param event the event to receive data from.
     * @sample onSample
     * @return a [Flow] providing the data received.
     */
    fun on(event: String): Flow<Array<Any>> = callbackFlow {
        // Create a listener that will send everything it receives from the socket
        // through the flow
        val lst = Emitter.Listener {
            trySend(it)
        }

        // Register that listener with the socket
        baseSocket.on(event, lst)

        // When the flow is closed, unregister the listener
        awaitClose {
            baseSocket.off(event, lst)
        }
    }

    /**
     * Emits an event.
     *
     * @param event the event name.
     * @param args any arguments.
     * @sample emitSample
     *
     * @see emitAndWait
     */
    fun emit(event: String, vararg args: Any) {
        baseSocket.emit(event, args)
    }

    /**
     * Emits an event and waits for an acknowledgement.
     *
     * @param event the event name.
     * @param args any arguments.
     *
     * @return the values of the acknowledgement sent by the server.
     *
     * @see emit
     */
    suspend fun emitAndWait(event: String, vararg args: Any): Array<out Any> = suspendCancellableCoroutine { cont ->
        baseSocket.emit(event, args) { res ->
            if (debug) {
                Log.d("tcas.sk#l", "listener called")
                Log.d("tcas.sk#l", "arg len: ${res.size}")
            }
            cont.resume(res)
        }
    }

    /**
     * Emits an event and waits for an acknowledgement.
     *
     * @param event the event name.
     * @param args any arguments.
     * @param timeout the time to wait for a response, in milliseconds.
     *
     * @return the values of the acknowledgement sent by the server.
     *
     * @throws AckTimeoutException the wait has timed out.
     *
     * @see emit
     */
    suspend fun emitAndWait(event: String, timeout: Long, vararg args: Any): Array<out Any> = suspendCancellableCoroutine { cont ->
        baseSocket.emit(event, args, object : AckWithTimeout(timeout) {
            override fun onTimeout() {
                cont.cancel(cause = AckTimeoutException("Timed out waiting for $timeout ms"))
            }

            override fun onSuccess(vararg args: Any) {
                cont.resume(args)
            }
        })
    }

    // Things exposed from the base socket follow
    @Suppress("unused")
    companion object {
        /**
         * Called on a connection.
         */
        const val EVENT_CONNECT = "connect"

        /**
         * Called on a disconnection.
         */
        const val EVENT_DISCONNECT = "disconnect"

        /**
         * Called on a connection error.
         *
         * Parameters:
         *  * (Exception) error data.
         *
         */
        const val EVENT_CONNECT_ERROR = "connect_error"

        const val EVENT_MESSAGE = "message"
    }

    /** Is this socket active? **/
    @Suppress("unused")
    val active: Boolean
        get() = this.baseSocket.isActive

    /** Is this socket connected? **/
    val connected: Boolean
        get() = this.baseSocket.connected()

    /**
     * The underlying engine.io socket ID.
     *
     * The value is present once the socket has connected, is removed when the socket disconnects and is updated if the socket reconnects.
     *
     * @return a socket ID
     */
    val id: String
        get() = this.baseSocket.id()

    /**
     * Connects the socket.
     */
    fun connect() {
        this.baseSocket.connect()
    }

    /**
     * Disconnects the socket.
     */
    fun disconnect() {
        this.baseSocket.disconnect()
    }
}