package es.unizar.webeng.hello.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

/**
 * Provides the [Clock] used to decide the time of day of a greeting.
 *
 * Injecting a clock instead of calling `LocalTime.now()` lets tests replace it
 * with a fixed clock and check every time slot deterministically.
 */
@Configuration
class ClockConfig {
    /**
     * Default clock: current instant in the server's system time zone.
     */
    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()
}
