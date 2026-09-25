package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.FixedClockConfig
import es.unizar.webeng.hello.service.GreetingService
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Checks that the page and `/api/hello` use `lang` and `Accept-Language`.
 * The clock is fixed at 09:00 UTC (morning).
 */
@WebMvcTest(HelloController::class, HelloApiController::class)
@Import(GreetingService::class, FixedClockConfig::class)
class GreetingMVCTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `api answers in Spanish with Accept-Language es`() {
        mockMvc
            .perform(get("/api/hello").param("name", "Ana").header(HttpHeaders.ACCEPT_LANGUAGE, "es"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message", equalTo("Buenos días, Ana!")))
            .andExpect(jsonPath("$.language", equalTo("es")))
            .andExpect(jsonPath("$.timeOfDay", equalTo("morning")))
            .andExpect(jsonPath("$.timestamp", equalTo("2026-09-25T09:00:00Z")))
    }

    @Test
    fun `api answers in Spanish with lang es`() {
        mockMvc
            .perform(get("/api/hello").param("name", "Ana").param("lang", "es"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message", equalTo("Buenos días, Ana!")))
            .andExpect(jsonPath("$.language", equalTo("es")))
    }

    @Test
    fun `api lang overrides Accept-Language`() {
        mockMvc
            .perform(
                get("/api/hello").param("name", "Ana").param("lang", "en").header(HttpHeaders.ACCEPT_LANGUAGE, "es"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.message", equalTo("Good morning, Ana!")))
            .andExpect(jsonPath("$.language", equalTo("en")))
    }

    @Test
    fun `api ignores unsupported lang and uses Accept-Language`() {
        mockMvc
            .perform(
                get("/api/hello").param("name", "Ana").param("lang", "fr").header(HttpHeaders.ACCEPT_LANGUAGE, "es-ES"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.language", equalTo("es")))
    }

    @Test
    fun `api defaults to English and World without name, lang or Accept-Language`() {
        mockMvc
            .perform(get("/api/hello"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message", equalTo("Good morning, World!")))
            .andExpect(jsonPath("$.language", equalTo("en")))
            .andExpect(jsonPath("$.timeOfDay", equalTo("morning")))
    }

    @Test
    fun `page greets in the language of Accept-Language`() {
        mockMvc
            .perform(get("/").param("name", "Ana").header(HttpHeaders.ACCEPT_LANGUAGE, "es-ES,es;q=0.9"))
            .andExpect(status().isOk)
            .andExpect(model().attribute("message", equalTo("Buenos días, Ana!")))
            .andExpect(model().attribute("language", equalTo("es")))
            .andExpect(model().attribute("timeOfDay", equalTo("morning")))
    }

    @Test
    fun `page lang overrides Accept-Language and renders language links`() {
        mockMvc
            .perform(get("/").param("name", "Ana").param("lang", "en").header(HttpHeaders.ACCEPT_LANGUAGE, "es"))
            .andExpect(status().isOk)
            .andExpect(model().attribute("message", equalTo("Good morning, Ana!")))
            .andExpect(content().string(containsString("<html lang=\"en\"")))
            .andExpect(content().string(containsString("href=\"/?name=Ana&amp;lang=es\"")))
    }

    @Test
    fun `page without name keeps the default message but shows the context`() {
        mockMvc
            .perform(get("/").header(HttpHeaders.ACCEPT_LANGUAGE, "es"))
            .andExpect(status().isOk)
            .andExpect(model().attribute("message", equalTo("Welcome to the Modern Web App!")))
            .andExpect(model().attribute("language", equalTo("es")))
    }
}
