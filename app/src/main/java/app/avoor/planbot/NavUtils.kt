package app.avoor.planbot

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder

class NavUtils {
    companion object {
        /**
         * Open a navigator app.
         *
         * @param address the address to go to.
         * @param placeID the Google Maps ID of the place to go to, optional and only affects Google Maps.
         */
        fun openNavigator(
            context: Context,
            address: String,
            placeID: String? = null
        ) {
            // Try to open Google Maps navigation
            val intent = getMapsNavigationIntent(address, placeID)
            context.startActivity(intent)
        }

        /**
         * Get an intent that opens Google Maps navigation.
         */
        @Suppress("MemberVisibilityCanBePrivate")
        fun getMapsNavigationIntent(
            address: String,
            placeID: String? = null
        ): Intent {
            val url =
                // start with the base url
                "https://www.google.com/maps/dir/?api=1" +
                // add the destination address
                "&destination=" + URLEncoder.encode(address, Charsets.UTF_8.name()) +
                // if there is a place ID, add it
                if (placeID != null)
                    "&destination_place_id=" + URLEncoder.encode(placeID, Charsets.UTF_8.name())
                // if it wasn't given, add nothing
                else ""

            return Intent(Intent.ACTION_VIEW, Uri.parse(url))
        }
    }
}