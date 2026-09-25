package es.unizar.webeng.hello.service

import es.unizar.webeng.hello.model.Greeting
import es.unizar.webeng.hello.model.Language
import es.unizar.webeng.hello.model.TimeOfDay
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.time.LocalTime
import java.util.Locale

/**
 * Builds greetings that depend on the server's time of day and the client's language.
 *
 * Controllers pass the raw request values (`lang` query parameter and
 * `Accept-Language` header) and this service decides the result, so the page
 * and the JSON API always agree.
 *
 * @property clock source of the current instant and time zone; replaced by a
 * fixed clock in tests.
 */
@Service
class GreetingService(
    private val clock: Clock,
) {
    /**
     * Builds the greeting for [name].
     *
     * The clock is read once, so [Greeting.timeOfDay] and [Greeting.timestamp]
     * always refer to the same instant.
     *
     * @param name name to greet, used as given.
     * @param lang value of the `lang` query parameter, or `null` if absent.
     * @param acceptLanguage value of the `Accept-Language` header, or `null` if absent.
     */
    fun greet(
        name: String,
        lang: String?,
        acceptLanguage: String?,
    ): Greeting {
        val now = clock.instant()
        val language = resolveLanguage(lang, acceptLanguage)
        val timeOfDay = timeOfDayAt(now)
        return Greeting("${language.salutation(timeOfDay)}, $name!", language, timeOfDay, now)
    }

    /**
     * Chooses the language with this priority:
     * 1. [lang], if it names a supported language;
     * 2. the best supported language in [acceptLanguage], respecting q-values;
     * 3. [Language.DEFAULT] (English).
     *
     * The header is read directly instead of using `HttpServletRequest.getLocale()`,
     * because that method falls back to the JVM default locale when the header is
     * missing, and the default must be English whatever the server locale is.
     */
    fun resolveLanguage(
        lang: String?,
        acceptLanguage: String?,
    ): Language = Language.fromTag(lang) ?: fromAcceptLanguage(acceptLanguage) ?: Language.DEFAULT

    /**
     * Returns the time slot of [instant] in the clock's time zone.
     */
    fun timeOfDayAt(instant: Instant): TimeOfDay = TimeOfDay.of(LocalTime.ofInstant(instant, clock.zone))

    /**
     * Returns the highest-priority supported language in an `Accept-Language`
     * [header], or `null` if the header is missing, malformed or names none.
     */
    private fun fromAcceptLanguage(header: String?): Language? {
        if (header.isNullOrBlank()) return null
        val ranges =
            try {
                Locale.LanguageRange.parse(header)
            } catch (e: IllegalArgumentException) {
                return null
            }
        return Language.fromTag(Locale.lookupTag(ranges, SUPPORTED_TAGS))
    }

    private companion object {
        val SUPPORTED_TAGS = Language.entries.map { it.code }
    }
}
