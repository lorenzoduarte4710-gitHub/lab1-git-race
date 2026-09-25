package es.unizar.webeng.hello.model

import java.util.Locale

/**
 * Languages the greeting supports.
 *
 * @property code ISO 639-1 code accepted in the `lang` query parameter and
 * returned in the JSON response and the page.
 */
enum class Language(
    val code: String,
) {
    /** Spanish. */
    ES("es"),

    /** English, also the default language. */
    EN("en"),
    ;

    /**
     * Returns the salutation for [timeOfDay] in this language, e.g.
     * `"Buenos días"` for [ES] and [TimeOfDay.MORNING].
     *
     * The nested `when` is exhaustive, so adding a language or a time slot
     * does not compile until every combination has a text.
     */
    fun salutation(timeOfDay: TimeOfDay): String =
        when (this) {
            ES ->
                when (timeOfDay) {
                    TimeOfDay.MORNING -> "Buenos días"
                    TimeOfDay.AFTERNOON -> "Buenas tardes"
                    TimeOfDay.NIGHT -> "Buenas noches"
                }
            EN ->
                when (timeOfDay) {
                    TimeOfDay.MORNING -> "Good morning"
                    TimeOfDay.AFTERNOON -> "Good afternoon"
                    TimeOfDay.NIGHT -> "Good evening"
                }
        }

    companion object {
        /** Language used when neither `lang` nor `Accept-Language` names a supported one. */
        val DEFAULT = EN

        /**
         * Finds the language for a BCP 47 [tag] such as `es`, `ES` or `es-ES`.
         * Only the primary language subtag is compared, ignoring case.
         *
         * @return the matching language, or `null` if [tag] is null, blank,
         * malformed or not supported.
         */
        fun fromTag(tag: String?): Language? {
            if (tag.isNullOrBlank()) return null
            val code = Locale.forLanguageTag(tag.trim()).language
            return entries.firstOrNull { it.code == code }
        }
    }
}
