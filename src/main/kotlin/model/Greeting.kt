package es.unizar.webeng.hello.model

import java.time.Instant

/**
 * Greeting decided by the server for one request.
 *
 * @property message text shown to the user, e.g. `"Buenos días, Ana!"`.
 * @property language language the message is written in.
 * @property timeOfDay time slot used to choose the salutation.
 * @property timestamp instant the decision was made; the same instant
 * determines [timeOfDay].
 */
data class Greeting(
    val message: String,
    val language: Language,
    val timeOfDay: TimeOfDay,
    val timestamp: Instant,
)
