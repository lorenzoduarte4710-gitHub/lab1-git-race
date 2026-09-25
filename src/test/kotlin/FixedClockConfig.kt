package es.unizar.webeng.hello

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

/**
 * Replaces the system clock with [CLOCK] so greetings do not depend on when tests run.
 */
@TestConfiguration
class FixedClockConfig {
    @Bean
    @Primary
    fun fixedClock(): Clock = CLOCK

    companion object {
        /** 09:00 UTC, i.e. morning. UTC keeps the result independent of the machine's time zone. */
        val CLOCK: Clock = Clock.fixed(Instant.parse("2026-09-25T09:00:00Z"), ZoneOffset.UTC)
    }
}
