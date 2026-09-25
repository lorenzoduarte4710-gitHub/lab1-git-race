package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.service.GreetingService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Serves the HTML home page (`welcome.html`).
 *
 * @property message default message shown when no name is given (`app.message`).
 * @property greetingService decides the time- and language-aware greeting.
 */
@Controller
class HelloController(
    @param:Value("\${app.message:Hello World}")
    private val message: String,
    private val greetingService: GreetingService,
) {
    /**
     * Renders the home page.
     *
     * Model attributes: `message` (the greeting if [name] is not blank,
     * otherwise [message]), `name`, `language` (`es`/`en`) and
     * `timeOfDay` (`morning`/`afternoon`/`night`).
     *
     * @param name name to greet; empty by default.
     * @param lang optional language code (`es` or `en`); has priority over [acceptLanguage].
     * @param acceptLanguage `Accept-Language` request header, if present.
     */
    @GetMapping("/")
    fun welcome(
        model: Model,
        @RequestParam(defaultValue = "") name: String,
        @RequestParam(required = false) lang: String?,
        @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE, required = false) acceptLanguage: String?,
    ): String {
        val greeting = greetingService.greet(name, lang, acceptLanguage)
        model.addAttribute("message", if (name.isNotBlank()) greeting.message else message)
        model.addAttribute("name", name)
        model.addAttribute("language", greeting.language.code)
        model.addAttribute("timeOfDay", greeting.timeOfDay.code)
        return "welcome"
    }
}

/**
 * JSON API for the greeting.
 *
 * @property greetingService decides the time- and language-aware greeting.
 */
@RestController
class HelloApiController(
    private val greetingService: GreetingService,
) {
    /**
     * `GET /api/hello` returns, for example:
     *
     * ```json
     * {"message":"Buenos días, Ana!","timestamp":"2026-09-25T09:00:00Z","language":"es","timeOfDay":"morning"}
     * ```
     *
     * @param name name to greet; `World` by default.
     * @param lang optional language code (`es` or `en`); has priority over [acceptLanguage].
     * @param acceptLanguage `Accept-Language` request header, if present.
     */
    @GetMapping("/api/hello", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun helloApi(
        @RequestParam(defaultValue = "World") name: String,
        @RequestParam(required = false) lang: String?,
        @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE, required = false) acceptLanguage: String?,
    ): Map<String, String> {
        val greeting = greetingService.greet(name, lang, acceptLanguage)
        return mapOf(
            "message" to greeting.message,
            "timestamp" to greeting.timestamp.toString(),
            "language" to greeting.language.code,
            "timeOfDay" to greeting.timeOfDay.code,
        )
    }
}
