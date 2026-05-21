# CLAUDE.md — JavaAppLogTesting

This document describes the codebase structure, development workflows, and conventions for AI assistants working on this repository.

## Project Overview

A Spring Boot 3.2.5 web application designed as a **testing and demonstration platform** for:
- Logging behavior (SLF4J + Logback with file and console appenders)
- CI/CD pipelines with intentional build/test failures
- AI-powered build analysis via GitHub Actions (GitHub Models API / gpt-4o-mini)
- Common Java runtime and compile-time error patterns

The codebase contains **intentional bugs** for educational and testing purposes. Many classes are expected to fail. Do not "fix" bugs unless explicitly asked to — they exist by design.

---

## Repository Structure

```
JavaAppLogTesting/
├── .github/workflows/
│   ├── build.yml                  # Standard CI: build + test + artifact upload
│   └── ai-build-summary.yml       # AI-powered CI: analyzes failures, posts PR comments
├── src/
│   ├── main/java/com/example/demo/
│   │   ├── DemoApplication.java   # Spring Boot entry point (@SpringBootApplication)
│   │   ├── HelloController.java   # REST endpoints: GET / and GET /greet?name=X
│   │   ├── UserService.java       # [BUG] NullPointerException: userRepository not injected
│   │   ├── UserRepository.java    # Repository interface (no implementation)
│   │   ├── CatalogService.java    # [BUG] ConcurrentModificationException + ClassCastException
│   │   ├── OrderProcessor.java    # [BUG] Compile error: undefined calculateDiscount() method
│   │   ├── DataParser.java        # [BUG] NumberFormatException + ArrayIndexOutOfBoundsException
│   │   ├── TransferService.java   # [BUG] Deadlock (lock inversion) + IllegalStateException
│   │   └── RecursiveProcessor.java# [BUG] StackOverflowError (infinite recursion paths)
│   ├── main/resources/
│   │   ├── application.properties # Server port, log levels, actuator endpoints
│   │   └── logback-spring.xml     # Console + rolling file appenders
│   └── test/java/com/example/demo/
│       ├── HelloControllerTest.java   # Passes — WebMvcTest for GET / and GET /greet
│       └── UserServiceTest.java       # Fails — NullPointerException (reflects UserService bug)
├── Dockerfile                     # Multi-stage Alpine build (JDK 17 build → JRE runtime)
├── render.yaml                    # Render.com deployment (Docker, free plan, /actuator/health)
└── pom.xml                        # Maven build (Spring Boot 3.2.5, Java 17)
```

---

## Build & Run

### Prerequisites
- Java 17+
- Maven 3.9+

### Common Commands

```bash
# Build (compile errors are reported but build continues — failOnError: false)
mvn clean compile

# Run all tests (test failures are reported but do not fail the build — testFailureIgnore: true)
mvn test

# Package JAR
mvn clean package -DskipTests

# Run the application
mvn spring-boot:run
# or
java -jar target/*.jar

# Full verify (compile + test + package)
mvn clean verify
```

### Ports & Endpoints

| Endpoint | Description |
|---|---|
| `GET /` | Returns greeting string |
| `GET /greet?name=X` | Returns personalized greeting |
| `GET /actuator/health` | Spring Boot health check |
| `GET /actuator/info` | Application info |

Default port: `8080` (override with `PORT` env variable).

---

## Intentional Bugs Reference

These bugs exist by design. Do not fix them unless the task explicitly requires it.

| File | Bug Type | Error Thrown |
|---|---|---|
| `UserService.java` | `userRepository` field not `@Autowired` / not injected | `NullPointerException` |
| `CatalogService.java` | `removeExpiredItems()` modifies List during iteration | `ConcurrentModificationException` |
| `CatalogService.java` | `sumPrices()` uses raw `List` with mixed types | `ClassCastException` |
| `OrderProcessor.java` | Calls `calculateDiscount()` which is never defined | Compile error: `cannot find symbol` |
| `DataParser.java` | `parseQuantity()` parses without input validation | `NumberFormatException` |
| `DataParser.java` | `extractThirdColumn()` accesses index without bounds check | `ArrayIndexOutOfBoundsException` |
| `TransferService.java` | `transferAtoB()` / `transferBtoA()` acquire locks in opposite order | Deadlock |
| `TransferService.java` | `validateAndTransfer()` operates without checking `isClosed` flag | `IllegalStateException` |
| `RecursiveProcessor.java` | `factorial(n)` has no base case for negative `n` | `StackOverflowError` |
| `RecursiveProcessor.java` | `countDown()` has mutual recursion with no termination | `StackOverflowError` |

---

## Logging Configuration

Logging uses **SLF4J + Logback** configured in `logback-spring.xml`.

### Log Destinations

| Appender | Output | Level | Retention |
|---|---|---|---|
| Console | stdout | ALL | — |
| File: `logs/app.log` | Rolling file | ALL | 10 MB/file, 7 days |
| File: `logs/error.log` | Rolling file | ERROR only | 10 MB/file, 30 days |

### Log Levels by Package

| Logger | Level |
|---|---|
| `com.example.demo` | DEBUG |
| `org.springframework.web` | INFO |
| Root | INFO |

### Logging Conventions

- Use SLF4J: `private static final Logger logger = LoggerFactory.getLogger(ClassName.class);`
- Use parameterized messages, never string concatenation:
  ```java
  // Correct
  logger.debug("Processing user {}", userId);
  // Wrong
  logger.debug("Processing user " + userId);
  ```
- Level usage: `DEBUG` for detailed tracing, `INFO` for normal flow, `WARN` for recoverable issues, `ERROR` for failures with stack traces.

---

## Code Conventions

### Package Structure
Single flat package: `com.example.demo`. No sub-packages.

### Spring Annotations
- `@SpringBootApplication` — main class only
- `@RestController` — REST API controllers
- `@Service` — service layer classes
- `@Component` — generic Spring-managed beans
- `@Autowired` — dependency injection (field injection used in this project)

### Java Version
Java 17 (pom.xml declares `<java.version>17</java.version>`). GitHub Actions CI uses Java 21.

### Dependencies
Kept minimal. Only Spring Boot starters and no external business libraries:
- `spring-boot-starter-web` — REST API
- `spring-boot-starter-actuator` — health/info endpoints
- `spring-boot-starter-test` — JUnit 5, Mockito, Spring Test

---

## Testing

### Framework
JUnit 5 (Jupiter) with Spring Test.

### Test Classes

| Class | Type | Status | Notes |
|---|---|---|---|
| `HelloControllerTest` | `@WebMvcTest` (integration) | Passes | Tests home and greet endpoints with `MockMvc` |
| `UserServiceTest` | Unit | Fails (by design) | Exposes the missing `@Autowired` bug in `UserService` |

### Running Tests

```bash
mvn test
```

Test reports are written to `target/surefire-reports/`. The Surefire plugin is configured with `testFailureIgnore: true`, so test failures do not stop the build — all results are always collected.

---

## CI/CD Workflows

### `build.yml` — Standard CI
- **Triggers:** push and pull_request on all branches
- **Java:** 17 with Maven cache
- **Steps:** compile → test → upload surefire reports → upload JAR artifact

### `ai-build-summary.yml` — AI-Powered CI
- **Triggers:** push to `main`, `master`, `develop` only
- **Java:** 21
- **Steps:**
  1. Auto-detect Maven or Gradle
  2. Run build with `continue-on-error: true`
  3. Collect all log artifacts (surefire reports, build log, error log)
  4. Call GitHub Models API (gpt-4o-mini) to analyze failures
  5. Post Markdown summary as PR comment (on pull requests)
  6. Post summary to GitHub Actions run summary (on push)
  7. Propagate original exit code (build still fails if it failed)

The AI summary includes: build status, exception root causes, test pass/fail counts, and recommended fixes per failure.

---

## Deployment

### Docker
Multi-stage Alpine build:
- Stage 1 (build): `maven:3.9-eclipse-temurin-17-alpine` — compiles and packages JAR
- Stage 2 (runtime): `eclipse-temurin:17-jre-alpine` — runs the JAR
- Exposed port: `8080`

```bash
docker build -t javaapplogtesting .
docker run -p 8080:8080 javaapplogtesting
```

### Render.com
Configured in `render.yaml`:
- Service type: Web
- Runtime: Docker
- Plan: Free
- Health check path: `/actuator/health`
- Auto-deploy: enabled on push

---

## Development Branch

Active development branch: `claude/add-claude-documentation-ssgJm`
Default remote branch: `origin/master`

Always develop on the designated feature branch and push there. Do not push to `master` directly.

---

## Key Reminders for AI Assistants

1. **Bugs are intentional.** Do not fix `UserService`, `CatalogService`, `OrderProcessor`, `DataParser`, `TransferService`, or `RecursiveProcessor` unless the task explicitly asks for it.
2. **Build is configured to continue on failure.** Both `failOnError: false` (compiler) and `testFailureIgnore: true` (surefire) are intentional — they ensure all failures are captured for analysis.
3. **No README.md exists.** This `CLAUDE.md` is the primary documentation.
4. **Test results matter.** The CI pipeline collects `target/surefire-reports/` — always run `mvn test` to generate them.
5. **Logging is part of the feature.** Changes to log levels or appenders may affect CI analysis behavior.
6. **Push to the feature branch**, not to `master`.
