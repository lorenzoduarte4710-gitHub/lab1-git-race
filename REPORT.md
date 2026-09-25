# Lab 1 Git Race -- Project Report

This note uses the same disclosure fields as the group-project **AI use (10%)** slice. Lab 1 is still **limited**: assistive GenAI only — not a full or substantial generated solution. The project will later expect agents plus AGENTS.md and one skill; you do **not** need those here.

Do not invent a percentage of “AI vs original” lines. Empty or fake disclosure fails this lab.

## What I specified

Before writing any code, I specified a greeting decided by the server from the time of day and the language. The time slots use the server clock: morning 06:00–11:59, afternoon 12:00–19:59 and night 20:00–05:59. The languages are Spanish and English (es, en), chosen first from the query parameter (lang), then from the request header (Accept-Language), and English by default. For example, Spanish in the morning gives (Buenos días, {name}!) and English at night gives (Good evening, {name}!). The greeting appears on the page (welcome.html) and in the JSON endpoint (/api/hello), which keeps its fields and adds two new ones (language, timeOfDay). The name parameter and its default value (World) work as before.

I also asked for the logic in a separate service, an injected clock (java.time.Clock) so tests can fix the time, and enums for the language and the time of day. I would know it works if unit tests with a fixed clock pass for every slot and its limits (11:59/12:00, 05:59/06:00), each language, an unsupported language and no header; if MVC tests pass with the header and with the parameter; and if the Gradle check passes (./gradlew check).

## What I changed

I added a clock bean (config/ClockConfig.kt), two enums (model/Language.kt, model/TimeOfDay.kt), a result class (model/Greeting.kt) and the service that builds the greeting (service/GreetingService.kt). Both controllers (controller/HelloController.kt) now use the service. The page (welcome.html) shows the greeting, the language, the time of day and two links (ES | EN) to change the language. I also added a README section, a test helper with a fixed clock (FixedClockConfig.kt) and two test classes (GreetingServiceTests.kt, GreetingMVCTests.kt). No new properties or dependencies were added.

For example, a request for Ana with the Spanish header at 19:36 returned ({"message":"Buenas tardes, Ana!","timestamp":"2026-09-25T17:36:30.306325100Z","language":"es","timeOfDay":"afternoon"}). The page without a name still shows the default message (app.message), as before.

## Technical decisions

I chose a separate service so the page and the API share the same logic, an injected clock so tests do not depend on the current time, and enums so invalid values cannot exist. I kept the JSON as a map of strings to change the existing tests as little as possible.

I also accepted some proposals from the assistant. The language header is read directly instead of using the request locale, because without a header that locale falls back to the JVM default, which could be Spanish on a Spanish machine. The header is parsed with JDK methods (Locale.LanguageRange, Locale.lookupTag) that respect preferences and regions, so (fr, es;q=0.8) and (es-ES) both give Spanish. A malformed header gives English, not an error. The tests use a clock fixed at 09:00 UTC so they do not depend on the machine's time zone.

I rejected Spring message files (MessageSource) because they add infrastructure for only two languages. I did not add a cache header (Vary: Accept-Language); it is a known limitation.

## How I verified

Before the increment, the build (./gradlew build) failed because a development dependency (spring-boot-devtools) had no version, and I fixed it in the build file (build.gradle.kts). After the increment, the Gradle check (./gradlew check) passed 45 tests with no failures, and it also passed on each of the five commits separately. I also started the application (./gradlew bootRun) and tested it with curl (curl -H "Accept-Language: es" "http://localhost:8080/api/hello?name=Ana").

No new test failed, but three existing test files had to change, because the old greeting (Hello, {name}!) now depends on the time. They now use the fixed clock and expect the morning greeting (Good morning, {name}!). The MVC tests also needed to import the service.

## AI disclosure

- **Tools / skills:** Claude Code (VS Code extension, model Claude Opus 5.5).
- **Purpose:** It proposed a design from my specification, then implemented the code, the tests, the KDoc and the README section, ran the checks, made the commits and the merge, created my GitHub fork and drafted this report, all at my request. The guide allows only assistive use, but in practice the assistant wrote most of the code and this report. My part was the specification, the design constraints and the decisions above. Every commit made by the assistant has a co-author line (Co-Authored-By: Claude).
- **Representative prompts:** My first prompt, translated from Spanish: "Read docs/GUIDE.md and the existing code, and start by proposing the design. Wait for my OK before writing code." It also contained the specification and the list of tests. Later: "Yes to everything, you do it" and "No, you are going to write it" (for this report).
- **Affected files/sections:** All the files listed above and this report, in five commits and one merge commit.
- **Validation steps:** The checks in the How I verified section, run by the assistant in my environment.
- **Citations:** No external code; only JDK and Spring APIs.
- **Human-reviewed:** I wrote the specification and chose the design options. I kept the generated code and did not edit it by hand.
