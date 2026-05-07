package tech.cataspect.socketx

import io.socket.client.IO
import org.json.JSONObject

val options = IO.Options.builder().build()
// sample data
val username = "kitten"

/**
 * Sample that demonstrates the [Socket.on] function.
 */
suspend fun onSample() {
    val socket = Socket("https://chat.example.com", options)
    socket.on("message").collect {
        // Get the username and the message
        val username = it[0] as String
        val message = it[1] as String
        // Show the message
        println("$username: $message")
    }
}

/**
 * Sample that demonstrates the [Socket.emit] function.
 */
fun emitSample(message: String) {
    val socket = Socket("https://chat.example.com", options)
    socket.emit("message", username, message)
    // the server will receive the username and message
    // as the 1st and 2nd arguments respectively

    // alternatively, send a JSON object:
    val body = mapOf("username" to username, "message" to message)
    socket.emit("message", JSONObject(body))
    // this object will look like {"username":"kitten","message":"hey"}
}