# QA Automation Test Suite

A comprehensive test automation framework covering REST API, GraphQL API and UI testing using Java, JUnit 5, REST Assured, and Playwright.

## Prerequisites

- Java 17+
- Maven 3.8+
- Chrome/Chromium browser (auto-installed by Playwright on first run)

## Project Structure

```
src/test/java/
├── api/
│   ├── tests/
│   │   ├── BookingApiTest.java      # REST API CRUD, search, negative, validation tests
│   │   ├── AuthenticationTest.java  # Auth endpoint tests
│   │   └── GraphQLTest.java         # GraphQL positive & negative scenarios
│   ├── models/
│   │   ├── Booking.java             # Booking request/response model
│   │   └── AuthResponse.java        # Auth response model
│   └── helpers/
│       ├── ApiHelper.java           # REST Assured helper for Restful Booker
│       └── GraphQLHelper.java       # GraphQL query helper
├── ui/
│   ├── tests/
│   │   ├── FormTest.java            # DemoQA practice form tests
│   │   └── WebTableTest.java        # DemoQA web table tests
│   ├── pages/
│   │   ├── FormPage.java            # Practice form page object
│   │   └── WebTablePage.java        # Web table page object
│   └── base/
│       └── BaseUITest.java          # Playwright browser lifecycle
├── integration/
│   └── ApiUiIntegrationTest.java    # API + UI cross-layer tests
└── config/
    └── TestConfig.java              # Centralized configuration
```

## How to Run

```bash
# Run all tests
mvn clean test

# Run only API tests (REST + GraphQL)
mvn test -Papi

# Run only UI tests
mvn test -Pui

# Run only integration tests
mvn test -Pintegration

# Run UI tests with visible browser
mvn test -Pui -Dheadless=false

# Run a specific test class
mvn test -Dtest="api.tests.GraphQLTest"
```

## Configuration

All endpoints and credentials are configurable via system properties or environment variables:

| Property | Default | Description |
|----------|---------|-------------|
| `restful.booker.base.url` | `https://restful-booker.herokuapp.com` | Restful Booker API |
| `graphql.base.url` | `https://rickandmortyapi.com/graphql` | GraphQL API |
| `demoqa.base.url` | `https://demoqa.com` | DemoQA UI |
| `booking.username` | `admin` | Restful Booker username |
| `booking.password` | `password123` | Restful Booker password |

Override example:
```bash
mvn test -Papi -Drestful.booker.base.url=http://localhost:3000
```

## Test Strategy

**Layered approach prioritizing risk and coverage breadth:**

1. **API tests first** — fastest feedback loop, lowest flakiness, covers core business logic (CRUD, auth, validation, schema contracts). Includes both REST and GraphQL to demonstrate protocol versatility.

2. **UI tests second** — validates user-facing flows on DemoQA (form submission, table manipulation). Uses the Page Object pattern to keep tests readable and maintainable.

3. **Integration tests last** — proves data can flow across layers (API creates data → UI consumes it). Catches contract mismatches between backend and frontend.

**Key design decisions:**
- Full request/response logging via REST Assured filters for easy debugging
- JSON Schema validation for contract testing (not just field-by-field assertions)
- Allure reporting integration for rich test reports
- Configurable headless/headed mode for CI vs local debugging

## Challenges & Solutions

| Challenge | Solution |
|-----------|----------|
| Restful Booker API returns 418 (rate limiting) | Tests are written correctly per API spec; retries handle transient failures. Framework logs full request/response for RCA. |
| DemoQA ads/banners intercept clicks | `removeAdsAndOverlays()` runs JS to strip ad elements on page load. |
| `selectGender("Male")` matched "Female" too | Switched from substring `setHasText` to regex `^Male$` for exact matching. |
| SpaceX GraphQL API went offline (404) | Switched to Rick and Morty GraphQL API — reliable, public, same test coverage. |

## Reporting

The framework integrates with Allure. To generate and view a report:

```bash
mvn clean test
mvn allure:serve
```

## What I Would Add With More Time

- **Page Element pattern** — extend POM to improve scalability, reusability and maintainability
- **Upload test results to Slack** — implement uploading results to Slack or other team communication platform
- **Retry mechanism** — custom JUnit extension to retry flaky tests (especially against rate-limited APIs)
- **Parallel execution** — configure surefire for parallel test classes to reduce total run time
- **Docker Compose** — spin up a local Restful Booker instance to eliminate external API flakiness
- **CI/CD pipeline** — GitHub Actions workflow with Allure report publishing
- **API contract tests** — OpenAPI spec-driven validation using Pact or Spring Cloud Contract
- **Visual regression** — Playwright screenshot comparison for UI tests
- **Test data factory** — randomized test data generation (Faker) to avoid collisions
- **Environment profiles** — separate configs for dev/staging/prod with Maven profiles
- **Performance smoke tests** — basic response time assertions on critical API endpoints
