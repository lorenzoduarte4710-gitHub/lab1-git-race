package es.unizar.webeng.hello.model

import java.time.LocalTime

/**
 * Time slot of the day used to choose a salutation.
 *
 * - [MORNING]: 06:00–11:59
 * - [AFTERNOON]: 12:00–19:59
 * - [NIGHT]: 20:00–05:59
 */
enum class TimeOfDay {
    /** 06:00–11:59. */
    MORNING,

    /** 12:00–19:59. */
    AFTERNOON,

    /** 20:00–05:59. */
    NIGHT,
    ;

    /** Lower-case name exposed in the JSON response and the page, e.g. `morning`. */
    val code: String get() = name.lowercase()

    companion object {
        /**
         * Returns the slot that contains [time]. Only the hour matters,
         * so 11:59:59 is [MORNING] and 12:00:00 is [AFTERNOON].
         */
        fun of(time: LocalTime): TimeOfDay =
            when (time.hour) {
                in 6..11 -> MORNING
                in 12..19 -> AFTERNOON
                else -> NIGHT
            }
    }
}
