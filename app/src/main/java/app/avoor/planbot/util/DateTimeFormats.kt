package app.avoor.planbot.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

class DateTimeFormats {
    companion object {
        /**
         * A format that renders the hours and minutes (HH:mm).
         */
        val HOUR_MINUTE = LocalDateTime.Format {
            time(LocalTime.Format {
                hour(Padding.ZERO)
                char(':')
                minute(Padding.ZERO)
            })
        }
    }
}  