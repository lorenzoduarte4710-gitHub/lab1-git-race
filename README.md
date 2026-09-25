# Lab 1 Git Race

Individual starter for Web Engineering 2026–27. Stack matches the group project: **Java 25 LTS**, **Kotlin 2.4.0**, **Spring Boot 4.1.0**, **Gradle 9.6.0**, **Bootstrap 5.3.8**.

The assignment, AI rules, and deadline are in [`docs/GUIDE.md`](docs/GUIDE.md). Fill [`REPORT.md`](REPORT.md) before you submit.

## Run

Java 25 is required (`./gradlew` uses the wrapper). GitHub Codespaces is optional: fork the starter, then open the Codespace from your fork (`docs/GUIDE.md`).

```bash
git clone https://github.com/UNIZAR-30246-WebEngineering/lab1-git-race.git
cd lab1-git-race
./gradlew check
./gradlew bootRun
```

- UI: <http://localhost:8080>
- JSON: <http://localhost:8080/api/hello>
- Health: <http://localhost:8080/actuator/health>

```bash
./gradlew test
./gradlew test --tests "HelloControllerUnitTests"
```

## Time- and language-aware greeting

The server chooses the greeting from the time of day (server clock) and the language.
The home page (when `name` is given) and `/api/hello` show the same greeting.

| `timeOfDay` | Server time | `es` | `en` |
| --- | --- | --- | --- |
| `morning` | 06:00–11:59 | Buenos días | Good morning |
| `afternoon` | 12:00–19:59 | Buenas tardes | Good afternoon |
| `night` | 20:00–05:59 | Buenas noches | Good evening |

Language priority:

1. `lang` query parameter, if it is `es` or `en` (case-insensitive; `es-ES` counts as `es`).
2. `Accept-Language` header (q-values respected; malformed headers are ignored).
3. English.

```bash
curl -H "Accept-Language: es" "http://localhost:8080/api/hello?name=Ana"
# {"message":"Buenos días, Ana!","timestamp":"2026-09-25T07:00:00Z","language":"es","timeOfDay":"morning"}
curl "http://localhost:8080/api/hello?name=Ana&lang=en"
```

`name` still defaults to `World` in the API. Without `name`, the page keeps `app.message`,
but it always shows the resolved language and time of day, plus `ES | EN` links that set `lang`.

The logic lives in `GreetingService`, which receives a `java.time.Clock` bean (`ClockConfig`,
system time zone). Tests replace it with a fixed clock (`FixedClockConfig`):

```bash
./gradlew test --tests "GreetingServiceTests"
./gradlew test --tests "GreetingMVCTests"
```

## Layout

```
src/main/kotlin/HelloWorld.kt
src/main/kotlin/config/ClockConfig.kt
src/main/kotlin/controller/HelloController.kt
src/main/kotlin/model/Greeting.kt
src/main/kotlin/model/Language.kt
src/main/kotlin/model/TimeOfDay.kt
src/main/kotlin/service/GreetingService.kt
src/main/resources/templates/welcome.html
src/test/kotlin/FixedClockConfig.kt
src/test/kotlin/controller/HelloControllerUnitTests.kt
src/test/kotlin/controller/HelloControllerMVCTests.kt
src/test/kotlin/controller/GreetingMVCTests.kt
src/test/kotlin/service/GreetingServiceTests.kt
src/test/kotlin/IntegrationTest.kt
```

## License

MIT — see `LICENSE`.
