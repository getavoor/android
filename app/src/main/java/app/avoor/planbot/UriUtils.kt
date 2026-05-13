package app.avoor.planbot

import android.net.Uri

class UriUtils {
    companion object {
        /**
         * Check if the given URI is an Avoor URL.
         */
        fun isAvoorUrl(uri: Uri): Boolean {
            return uri.host == "avoor.app" || uri.host == "avoor-app.oa.r.appspot.com" || uri.scheme == "avoor"
        }
    }
}