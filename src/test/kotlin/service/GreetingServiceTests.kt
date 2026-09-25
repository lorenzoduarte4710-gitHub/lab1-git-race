package es.unizar.webeng.hello.service

import es.unizar.webeng.hello.model.Greeting
import es.unizar.webeng.hello.model.Language
import es.unizar.webeng.hello.model.TimeOfDay
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class GreetingServiceTests {
    /** Service whose clock is fixed at [time] (`HH:mm:ss`) UTC. */
    private fun serviceAt(time: String) =
        GreetingService(Clock.fixed(Instant.parse("2026-09-25T${time}Z"), ZoneOffset.UTC))

    private val service = serviceAt("09:00:00")

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource(
        "00:00:00, NIGHT",
        "05:59:59, NIGHT",
        "06:00:00, MORNING",
        "11:59:59, MORNING",
        "12:00:00, AFTERNOON",
        "19:59:59, AFTERNOON",
        "20:00:00, NIGHT",
        "23:59:59, NIGHT",
    )
    fun `maps each time to its slot, including the limits`(
        time: String,
        expected: TimeOfDay,
    ) {
        assertThat(serviceAt(time).greet("Ana", null, null).timeOfDay).isEqualTo(expected)
    }

    @ParameterizedTest(name = "{0} at {1} -> {2}")
    @CsvSource(
        "es, 09:00:00, 'Buenos días, Ana!'",
        "es, 15:00:00, 'Buenas tardes, Ana!'",
        "es, 22:00:00, 'Buenas noches, Ana!'",
        "en, 09:00:00, 'Good morning, Ana!'",
        "en, 15:00:00, 'Good afternoon, Ana!'",
        "en, 22:00:00, 'Good evening, Ana!'",
    )
    fun `builds the message for each language and time slot`(
        lang: String,
        time: String,
        expected: String,
    ) {
        assertThat(serviceAt(time).greet("Ana", lang, null).message).isEqualTo(expected)
    }

    @Test
    fun `greeting carries language, time slot and the clock instant`() {
        assertThat(service.greet("Ana", "es", null)).isEqualTo(
            Greeting("Buenos días, Ana!", Language.ES, TimeOfDay.MORNING, Instant.parse("2026-09-25T09:00:00Z")),
        )
    }

    @Test
    fun `uses the time zone of the clock`() {
        // 05:00 UTC is 07:00 in Madrid (UTC+2 in September): morning there, night in UTC.
        val madrid = GreetingService(Clock.fixed(Instant.parse("2026-09-25T05:00:00Z"), ZoneId.of("Europe/Madrid")))

        assertThat(madrid.greet("Ana", null, null).timeOfDay).isEqualTo(TimeOfDay.MORNING)
        assertThat(serviceAt("05:00:00").greet("Ana", null, null).timeOfDay).isEqualTo(TimeOfDay.NIGHT)
    }

    @Test
    fun `lang parameter has priority over Accept-Language`() {
        assertThat(service.resolveLanguage("en", "es")).isEqualTo(Language.EN)
        assertThat(service.resolveLanguage("es", "en")).isEqualTo(Language.ES)
    }

    @Test
    fun `lang ignores case and region subtags`() {
        assertThat(service.resolveLanguage("ES", null)).isEqualTo(Language.ES)
        assertThat(service.resolveLanguage("es-ES", null)).isEqualTo(Language.ES)
    }

    @Test
    fun `uses Accept-Language when lang is missing or blank`() {
        assertThat(service.resolveLanguage(null, "es-ES,es;q=0.9")).isEqualTo(Language.ES)
        assertThat(service.resolveLanguage("", "es")).isEqualTo(Language.ES)
    }

    @Test
    fun `unsupported lang falls back to Accept-Language`() {
        assertThat(service.resolveLanguage("fr", "es")).isEqualTo(Language.ES)
    }

    @Test
    fun `unsupported lang without Accept-Language defaults to English`() {
        assertThat(service.resolveLanguage("fr", null)).isEqualTo(Language.EN)
    }

    @Test
    fun `defaults to English without lang or Accept-Language`() {
        assertThat(service.resolveLanguage(null, null)).isEqualTo(Language.EN)
        assertThat(service.greet("Ana", null, null).message).isEqualTo("Good morning, Ana!")
    }

    @Test
    fun `picks the best supported language by q-value`() {
        assertThat(service.resolveLanguage(null, "fr, es;q=0.8, en;q=0.5")).isEqualTo(Language.ES)
        assertThat(service.resolveLanguage(null, "es;q=0.3, en;q=0.9")).isEqualTo(Language.EN)
    }

    @Test
    fun `defaults to English when Accept-Language has no supported language`() {
        assertThat(service.resolveLanguage(null, "fr-FR, de;q=0.8")).isEqualTo(Language.EN)
        assertThat(service.resolveLanguage(null, "*")).isEqualTo(Language.EN)
    }

    @Test
    fun `ignores a malformed Accept-Language header`() {
        assertThat(service.resolveLanguage(null, "@@not-a-language@@")).isEqualTo(Language.EN)
    }
}
