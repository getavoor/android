package app.avoor.planbot.data.discovery

import org.json.JSONObject

data class DiscoveryStartBody (
    /**
     * The restaurant's type (optional).
     */
    val type: String?
) {
    fun toJSON(): JSONObject {
        return JSONObject(
            mapOf("type" to type)
        )
    }
}


