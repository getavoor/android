package app.avoor.planbot

import io.socket.client.IO
import tech.cataspect.socketx.Socket

object SocketIOInstance {
    private const val BASE_URL = "https://avoor-app.oa.r.appspot.com/"
    private const val LOCAL_URL = "http://192.168.10.43:8080/"

    /**
     * Get a Socket.io socket.
     */
    fun getSocket(production: Boolean = true): Socket {
        val uri = if (production) BASE_URL else LOCAL_URL
        val options = IO.Options.builder() // ...
            .build()

        return Socket(uri, options)
    }
}