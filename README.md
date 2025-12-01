# QA Automation Assignment

A comprehensive test automation framework combining API and UI testing with parallel execution, retry policies, and detailed reporting.

## 🏗️ Project Architecture

```
qa-automation-assignment/
├── src/
│   ├── main/java/
│   └── test/java/
│       ├── api/
│       │   ├── data/         # Test data builders
│       │   ├── helpers/      # API helper methods
│       │   ├── models/       # Data models (POJOs)
│       │   ├── steps/        # Step definitions for Allure
│       │   └── tests/        # API test classes
│       ├── integration/      # API + UI integration tests
│       ├── ui/
│       │   ├── data/         # UI test data builders
│       │   ├── pages/        # Page Object Models
│       │   └── tests/        # UI test classes
│       └── config/           # Configuration management
└── src/test/resources/
    ├── config.properties     # Test configuration
    ├── junit-platform.properties  # JUnit settings
    └── schemas/             # JSON schema validators
```

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+
- Lombok
- Chrome browser (for UI tests)

### Run All Tests
```bash
  mvn clean test
```

### Run Specific Test Classes
```bash
  # API tests
  mvn test -Dtest=BookingApiTest
  mvn test -Dtest=AuthenticationTest

  # UI tests
  mvn test -Dtest=FormTest
  mvn test -Dtest=WebTableTest

  # Integration tests
  mvn test -Dtest=ApiUiIntegrationTest
```

### Run Tests by Tag
```bash
  # Run only API tests
  mvn test -Dgroups=api

  # Run only UI tests
  mvn test -Dgroups=ui
    
  # Run only integration tests
  mvn test -Dgroups=integration
```

## ⚙️ Configuration

### Test Configuration (`config.properties`)
```properties
# API Configuration
api.base.url=https://restful-booker.herokuapp.com
api.auth.username=admin
api.auth.password=password123

# UI Configuration
ui.base.url=https://demoqa.com
ui.browser=chrome
ui.headless=false
ui.timeout=10000
ui.screenshots.on.failure=true

# Test Configuration
test.retry.count=2
```

### Parallel Execution

**Configuration:** Tests run with **class-level parallelism** - test classes execute in parallel (up to 4 concurrent), but methods within each class run sequentially. This prevents shared state issues while maintaining good performance.

#### Run with Default Settings (4 parallel threads)
```bash
  mvn test
```

#### Change Number of Parallel Threads
```bash
  # Run with 8 parallel threads
  mvn test -Dtest.parallel.threads=8

  # Run with 2 parallel threads
  mvn test -Dtest.parallel.threads=2
```

#### Disable Parallel Execution
```bash
  mvn test -Dtest.parallel.enabled=false
```

#### Configuration Location
- **Maven**: `pom.xml` properties section
- **JUnit**: `junit-platform.properties`

```xml
<!-- pom.xml -->
<test.parallel.enabled>true</test.parallel.enabled>
<test.parallel.threads>4</test.parallel.threads>
```

```properties
# junit-platform.properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.classes.default=concurrent
junit.jupiter.execution.parallel.config.fixed.parallelism=4
```

### Retry Policy

**Implementation:** UI tests use JUnit Pioneer's `@RetryingTest` annotation for automatic retry on failure.

UI tests are configured to retry up to **3 times** to handle flaky tests caused by timing issues:

```java
@RetryingTest(3)
@DisplayName("Should submit student registration form successfully")
public void testCompleteFormSubmission() {
    // Test implementation
}
```

**Benefits:**
- Automatically retries failed tests up to 3 attempts
- Reduces false failures from timing/synchronization issues
- No custom extension code needed
- Clear visibility in test reports

### Headless Mode (for CI/CD)
```bash
  mvn test -Dui.headless=true
```

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

The framework includes a complete GitHub Actions workflow (`.github/workflows/test-automation.yml`) that:

#### Triggers
- **Push**: Automatically runs on push to master/main/develop branches
- **Pull Requests**: Validates PRs before merging
- **Scheduled**: Daily runs at 2 AM UTC for regression testing
- **Manual**: Can be triggered manually via GitHub Actions UI

#### Test Execution Strategy
The workflow runs tests in two modes:

1. **Parallel Test Suites** (Matrix Strategy)
   - API Tests (`@Tag("api")`)
   - UI Tests (`@Tag("ui")`)
   - Integration Tests (`@Tag("integration")`)
   - Each suite runs independently in parallel

2. **Complete Test Suite**
   - All tests run together with 4 parallel threads
   - Comprehensive regression validation

#### Features
- ✅ **Java 21** with Maven dependency caching
- ✅ **Chrome Browser** automatically installed for UI tests
- ✅ **Headless Mode** enabled for UI tests in CI
- ✅ **Allure Reports** automatically generated and published to GitHub Pages
- ✅ **Test History** tracks last 20 test runs
- ✅ **Artifacts** - Test results and screenshots uploaded for 30 days
- ✅ **Screenshot Capture** on UI test failures (retained for 7 days)
- ✅ **Continue on Failure** - Reports generated even when tests fail

#### Allure Reports on GitHub Pages

After the workflow runs, Allure reports are published to:
- **API Tests**: `https://yourusername.github.io/qa-automation-assignment/api/`
- **UI Tests**: `https://yourusername.github.io/qa-automation-assignment/ui/`
- **Integration Tests**: `https://yourusername.github.io/qa-automation-assignment/integration/`
- **All Tests**: `https://yourusername.github.io/qa-automation-assignment/all-tests/`

#### Setup Instructions
1. **Enable GitHub Pages**:
   - Go to Settings → Pages
   - Set Source to `gh-pages` branch
   - Save changes

2. **Push Workflow**:
   ```bash
   git add .github/workflows/test-automation.yml
   git commit -m "Add CI/CD pipeline with GitHub Actions"
   git push origin master
   ```

3. **View Results**:
   - Go to Actions tab in your GitHub repository
   - Click on the latest workflow run
   - View test results and download artifacts
   - Access Allure reports via GitHub Pages URL

## 📊 Allure Reports

### Generate and View Report (Recommended)
```bash
  # Generate report and open in browser
  mvn allure:serve
```

### Generate Report Only
```bash
  mvn allure:report
```

Then open: `target/allure-report/index.html`

### Full Test Run with Report
```bash
  mvn clean test allure:serve
```

### Report Features
- ✅ Test execution timeline
- ✅ Test case history
- ✅ Screenshots on failure
- ✅ Request/Response details for API tests
- ✅ Step-by-step execution details
- ✅ Test categorization by Epic/Feature
- ✅ Severity levels
- ✅ Environment information

## 🧪 Test Coverage

### API Tests (`api.tests`)
**Target**: Restful Booker API (https://restful-booker.herokuapp.com)

#### Authentication Tests
- ✅ Successful authentication
- ✅ Invalid credentials handling
- ✅ Empty credentials validation
- ✅ Wrong password handling
- ✅ Response message validation

#### Booking Management Tests
- ✅ Create booking with all fields
- ✅ Get booking by ID
- ✅ Update booking (PUT)
- ✅ Partial update booking (PATCH)
- ✅ Delete booking
- ✅ Get all bookings
- ✅ Filter bookings by firstname
- ✅ Filter bookings by lastname
- ✅ Non-existent booking (404)
- ✅ Unauthorized update (403)
- ✅ JSON schema validation
- ✅ Date format validation (yyyy-MM-dd)
- ✅ Invalid date scenarios
- ✅ Same check-in/check-out dates

**Total API Tests**: 18

### UI Tests (`ui.tests`)
**Target**: DemoQA (https://demoqa.com)

#### Practice Form Tests
- ✅ Complete form submission with all fields
- ✅ Form submission with minimal required fields
- ✅ State/City dependent dropdown validation
- ✅ Gender selection
- ✅ Hobbies multi-select
- ✅ Date picker interaction
- ✅ File upload
- ✅ Subjects autocomplete

#### Web Table Tests
- ✅ Add new record
- ✅ Edit existing record
- ✅ Delete record
- ✅ Search functionality
- ✅ Clear search
- ✅ Column sorting
- ✅ Record validation

**Total UI Tests**: 13

### Integration Tests (`integration`)
**Target**: Combined API + UI workflows

- ✅ Create booking via API → Add to Web Table via UI
- ✅ Sync multiple API bookings to Web Table
- ✅ Update booking via API → Reflect changes in Web Table
- ✅ Search Web Table using API booking data

**Total Integration Tests**: 4

### **Grand Total: 35 Tests**

## 🛠️ Technologies & Frameworks

### Core
- **Java 21** - Programming language
- **Maven** - Build & dependency management
- **JUnit 5** - Test framework
- **JUnit Pioneer** - Extended testing features (retry policy)

### API Testing
- **RestAssured** - API testing library
- **Jackson** - JSON serialization/deserialization
- **JSON Schema Validator** - Schema validation

### UI Testing
- **Selenide** - Simplified Selenium wrapper

### Reporting & Utilities
- **Allure** - Test reporting framework
- **AssertJ** - Fluent assertions
- **Lombok** - Reduce boilerplate code
- **Owner** - Configuration management
- **Apache Commons Lang3** - Utility functions

### CI/CD
- **GitHub Actions** - Automated CI/CD pipeline
- **GitHub Pages** - Allure report hosting

### Test Data & Patterns
- **Builder Pattern** with Lombok
- **Page Object Model** for UI tests
- **Step Pattern** for API tests with Allure
- **Test Data Builders** for data generation

## 📋 Test Strategy

### Approach
1. **Layered Testing**: Separate API, UI, and Integration test layers for maintainability
2. **Data Isolation**: Each test creates unique data to prevent conflicts in parallel execution
3. **Automatic Cleanup**: `@AfterEach` hooks ensure test data is cleaned up
4. **Thread Safety**: ThreadLocal for token storage, per-method test instances
5. **Parallel Execution**: Class-level parallelism balances speed with stability
6. **Comprehensive Coverage**: CRUD operations, validation, error handling, edge cases

### What I Prioritized

#### 1. **Maintainability**
   - Page Object Model for UI tests
   - Step definitions for API tests
   - Test Data Builders with Lombok
   - Configuration management via properties files
   - Clear separation of concerns

#### 2. **Reliability**
   - Automatic cleanup after each test
   - Thread-safe parallel execution
   - Retry policy for flaky UI tests (JUnit Pioneer @RetryingTest)
   - Unique test data generation
   - Proper wait strategies in UI tests

#### 3. **Reporting & Visibility**
   - Allure integration with detailed steps
   - Epic/Feature/Severity annotations
   - Screenshots on failure
   - Request/Response logging
   - Test execution timeline

#### 4. **Test Data Management**
   - Random data generation to avoid conflicts
   - Meaningful prefixes for debugging
   - Builder pattern for flexible test data
   - Schema validation for API responses

#### 5. **CI/CD Ready**
   - GitHub Actions workflow with matrix strategy
   - Headless mode support for UI tests
   - Configurable parallel execution
   - Automatic Allure report publishing to GitHub Pages
   - Test artifacts and screenshot retention
   - Scheduled daily regression runs
   - No hardcoded dependencies

## 🚧 Challenges & Solutions

### Challenge 1: Parallel Execution with Shared State
**Problem**: Tests were failing when running in parallel due to shared static `AuthenticateSteps` and `bookingHelper` instances.

**Solution**:
- Changed from method-level to class-level parallelism
- Used `junit.jupiter.execution.parallel.mode.default=same_thread`
- This allows multiple test classes to run concurrently while methods within each class run sequentially

### Challenge 2: Test Data Conflicts
**Problem**: Multiple tests creating/modifying the same booking IDs caused failures.

**Solution**:
- Implemented unique data generation using `RandomStringUtils.randomAlphabetic(3)`
- Added meaningful prefixes to identify test context (e.g., "Create", "Update", "Filter")
- Each test creates its own data and tracks it for cleanup

### Challenge 3: Flaky UI Tests
**Problem**: UI tests occasionally failed due to timing issues with dynamic content.

**Solution**:
- Used Selenide's built-in smart waiting
- Configured appropriate timeouts (10 seconds)
- Avoided explicit waits in favor of Selenide's implicit waiting
- Added proper element visibility checks

### Challenge 4: Token Management in Parallel Execution
**Problem**: API authentication token was being shared across parallel threads.

**Solution**:
- Implemented ThreadLocal storage in `AuthenticateSteps`
- Each thread gets its own authentication token
- Thread-safe token access prevents cross-contamination

### Challenge 5: Allure Lifecycle Errors in Parallel Mode
**Problem**: Allure was throwing lifecycle errors when tests ran in parallel.

**Solution**:
- Switched from concurrent method execution to concurrent class execution
- AspectJ weaver properly configured in Maven Surefire plugin
- Allure listeners configured at class level

### Challenge 6: Configuration Duplication
**Problem**: Same configuration values defined in multiple places (config.properties, pom.xml, junit-platform.properties).

**Solution**:
- Centralized Maven build properties in pom.xml `<properties>` section
- Kept only Java-accessed values in config.properties
- Used junit-platform.properties only for JUnit-specific settings
- Eliminated redundant configuration entries

## ✨ What I Would Add With More Time

### Testing Enhancements
1. **Negative Test Coverage**
   - Invalid data type testing (string instead of number)
   - Boundary value analysis for dates and prices
   - SQL injection and XSS testing for input fields
   - Rate limiting tests for API endpoints

2. **Performance Testing**
   - API response time assertions
   - UI page load time monitoring
   - Stress testing with high concurrent requests
   - Database query performance validation

3. **Visual Regression Testing**
   - Screenshot comparison for UI tests
   - Percy or Applitools integration
   - Cross-browser visual validation

4. **Accessibility Testing**
   - WCAG compliance checks
   - Keyboard navigation testing
   - Screen reader compatibility
   - Color contrast validation

5. **Security Testing**
   - OWASP Top 10 vulnerability scanning
   - Authentication/Authorization edge cases
   - Session management testing
   - Data encryption validation

### Framework Improvements
1. **Enhanced Reporting**
   - Custom Allure categories for failure types
   - Test execution trends over time
   - Flaky test detection and tracking
   - Performance metrics in reports

2. **Test Data Management**
   - Database seeding for consistent test data
   - Test data factory with predefined scenarios
   - External test data files (CSV/JSON)
   - Data cleanup strategies for failed tests

3. **Docker Integration**
   - Containerized test execution
   - Selenium Grid setup for cross-browser testing
   - API mock server for offline testing
   - Database containers for integration tests

4. **Enhanced CI/CD**
   - Slack/Email notifications for test results
   - Teamcity pipeline as alternative to GitHub Actions
   - Deployment to multiple environments (dev, staging, prod)
   - Integration with Jira for test case management

5. **Advanced Retry Logic**
   - Conditional retry (only for specific exceptions)
   - Exponential backoff for API retries
   - Retry analytics and reporting
   - Apply retry to API tests where appropriate

6. **Logging & Debugging**
   - Structured logging with Log4j2
   - Request/Response interceptors with detailed logging
   - Video recording for failed UI tests
   - Har file generation for network analysis

7. **Cross-Browser Testing**
   - Selenium Grid configuration
   - BrowserStack/Sauce Labs integration
   - Parameterized tests for multiple browsers
   - Browser-specific test configurations

8. **API Contract Testing**
   - Pact integration for contract testing
   - OpenAPI/Swagger schema validation
   - Mock server for consumer-driven contracts
   - API versioning tests

9. **Mobile Testing**
   - Appium setup for mobile web testing
   - Responsive design validation
   - Touch gesture testing
   - Device-specific test configurations

10. **Advanced Assertions**
    - Soft assertions for multiple validations
    - Custom assertion libraries
    - Database state verification
    - Email/SMS validation for notifications

**Happy Testing! 🚀**
