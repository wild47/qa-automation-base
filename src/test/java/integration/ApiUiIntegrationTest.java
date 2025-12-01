package integration;

import api.data.TestDataBuilder;
import api.models.Booking;
import api.models.BookingResponse;
import api.steps.AuthenticateSteps;
import api.steps.BookingSteps;
import config.ConfigProvider;
import io.qameta.allure.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import ui.pages.WebTablePage;
import ui.tests.BaseTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Integration Testing")
@Feature("E2E")
@Tag("integration")
public class ApiUiIntegrationTest extends BaseTest {

    private BookingSteps bookingHelper;
    private AuthenticateSteps authHelper;
    private WebTablePage webTablePage;
    private final List<Integer> createdBookingIds = new ArrayList<>();
    private static final String WEB_TABLES_URL = "/webtables";

    @BeforeEach
    public void setUp() {
        super.setUp();
        bookingHelper = new BookingSteps();
        authHelper = new AuthenticateSteps();
        webTablePage = new WebTablePage();

        // Authenticate for API calls using credentials from config
        authHelper.authenticate(
                ConfigProvider.getConfig().apiUsername(),
                ConfigProvider.getConfig().apiPassword()
        );
    }

    @AfterEach
    public void tearDown() {
        // Clean up all created bookings via API
        for (Integer bookingId : createdBookingIds) {
            try {
                bookingHelper.deleteBooking(bookingId);
            } catch (Exception e) {
                // Booking might already be deleted or not exist, continue with cleanup
            }
        }
        createdBookingIds.clear();
        super.tearDown();
    }

    @Test
    @DisplayName("Should create booking via API and add to Web Table via UI")
    @Description("Integration test: Create booking through API, then use that data to populate Web Table in UI")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateApiBookingAndAddToUiWebTable() {
        // Step 1: Create booking via API with unique data
        String firstName = "Api" + RandomStringUtils.randomAlphabetic(3);
        String lastName = "User" + RandomStringUtils.randomAlphabetic(3);
        Booking apiBooking = TestDataBuilder.createBooking(firstName, lastName, 500);

        BookingResponse bookingResponse = bookingHelper.createBooking(apiBooking);
        createdBookingIds.add(bookingResponse.getBookingId());

        assertThat(bookingResponse.getBookingId()).isNotNull();
        assertThat(bookingResponse.getBooking().getFirstName()).isEqualTo(apiBooking.getFirstName());
        assertThat(bookingResponse.getBooking().getLastName()).isEqualTo(apiBooking.getLastName());

        // Step 2: Open Web Table UI
        webTablePage.open(ConfigProvider.getConfig().uiBaseUrl() + WEB_TABLES_URL);

        // Step 3: Add booking data from API to Web Table
        String email = apiBooking.getFirstName().toLowerCase() + "." +
                       apiBooking.getLastName().toLowerCase() + "@booking.com";

        webTablePage.addRecord(
                apiBooking.getFirstName(),
                apiBooking.getLastName(),
                email,
                "30",
                String.valueOf(apiBooking.getTotalPrice()),
                "Customer"
        );

        // Step 4: Verify the record exists in Web Table
        assertThat(webTablePage.isRecordPresent(email))
                .as("Booking from API should be present in Web Table")
                .isTrue();

        webTablePage.verifyRecordData(email, apiBooking.getFirstName(), apiBooking.getLastName());
    }

    @Test
    @DisplayName("Should sync multiple API bookings to Web Table")
    @Description("Integration test: Create multiple bookings via API and sync them all to Web Table UI")
    @Severity(SeverityLevel.NORMAL)
    public void testSyncMultipleApiBookingsToUiWebTable() {
        // Step 1: Create multiple bookings via API with unique data
        String firstName1 = "Multi" + RandomStringUtils.randomAlphabetic(3);
        String lastName1 = "One" + RandomStringUtils.randomAlphabetic(3);
        String firstName2 = "Multi" + RandomStringUtils.randomAlphabetic(3);
        String lastName2 = "Two" + RandomStringUtils.randomAlphabetic(3);

        Booking booking1 = TestDataBuilder.createBooking(firstName1, lastName1, 300);
        Booking booking2 = TestDataBuilder.createBooking(firstName2, lastName2, 450);

        BookingResponse response1 = bookingHelper.createBooking(booking1);
        BookingResponse response2 = bookingHelper.createBooking(booking2);

        createdBookingIds.add(response1.getBookingId());
        createdBookingIds.add(response2.getBookingId());

        assertThat(response1.getBookingId()).isNotNull();
        assertThat(response2.getBookingId()).isNotNull();

        // Step 2: Open Web Table UI
        webTablePage.open(ConfigProvider.getConfig().uiBaseUrl() + WEB_TABLES_URL);

        // Step 3: Add both bookings to Web Table
        String email1 = booking1.getFirstName().toLowerCase() + "@booking.com";
        String email2 = booking2.getFirstName().toLowerCase() + "@booking.com";

        webTablePage.addRecord(
                booking1.getFirstName(),
                booking1.getLastName(),
                email1,
                "28",
                String.valueOf(booking1.getTotalPrice()),
                "Customer"
        );

        webTablePage.addRecord(
                booking2.getFirstName(),
                booking2.getLastName(),
                email2,
                "35",
                String.valueOf(booking2.getTotalPrice()),
                "VIP Customer"
        );

        // Step 4: Verify both records exist
        assertThat(webTablePage.isRecordPresent(email1))
                .as("First booking should be synced to Web Table")
                .isTrue();
        assertThat(webTablePage.isRecordPresent(email2))
                .as("Second booking should be synced to Web Table")
                .isTrue();

        webTablePage.verifyRecordData(email1, booking1.getFirstName(), booking1.getLastName());
        webTablePage.verifyRecordData(email2, booking2.getFirstName(), booking2.getLastName());
    }

    @Test
    @DisplayName("Should update booking via API and reflect changes in Web Table")
    @Description("Integration test: Create booking via API, add to UI, update via API, then verify UI can be updated too")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateApiBookingAndSyncToWebTable() {
        // Step 1: Create initial booking via API with unique data
        String firstName = "Update" + RandomStringUtils.randomAlphabetic(3);
        String lastName = "Test" + RandomStringUtils.randomAlphabetic(3);
        Booking originalBooking = TestDataBuilder.createBooking(firstName, lastName, 350);

        BookingResponse bookingResponse = bookingHelper.createBooking(originalBooking);
        createdBookingIds.add(bookingResponse.getBookingId());

        assertThat(bookingResponse.getBookingId()).isNotNull();

        // Step 2: Add to Web Table UI
        webTablePage.open(ConfigProvider.getConfig().uiBaseUrl() + WEB_TABLES_URL);

        String email = originalBooking.getFirstName().toLowerCase() + "@booking.com";
        webTablePage.addRecord(
                originalBooking.getFirstName(),
                originalBooking.getLastName(),
                email,
                "32",
                String.valueOf(originalBooking.getTotalPrice()),
                "Customer"
        );

        assertThat(webTablePage.isRecordPresent(email)).isTrue();

        // Step 3: Update booking via API (change price)
        Booking updatedBooking = TestDataBuilder.createBooking(firstName, lastName, 400);
        bookingHelper.updateBooking(bookingResponse.getBookingId(), updatedBooking);

        // Step 4: Update the record in Web Table to reflect API changes
        webTablePage
                .editRecord(email)
                .updateSalary("400")  // Simulating price update
                .submitForm();

        // Step 5: Verify the update
        webTablePage.verifyRecordData(email, updatedBooking.getFirstName(), updatedBooking.getLastName());
    }

    @Test
    @DisplayName("Should search Web Table using API booking data")
    @Description("Integration test: Create booking via API, add to UI, then search for it using API data")
    @Severity(SeverityLevel.MINOR)
    public void testSearchWebTableUsingApiData() {
        // Step 1: Create unique booking via API
        String uniqueFirstName = "Search" + RandomStringUtils.randomAlphabetic(3);
        String lastName = "Test" + RandomStringUtils.randomAlphabetic(3);

        Booking apiBooking = TestDataBuilder.createBooking(uniqueFirstName, lastName, 550);

        BookingResponse bookingResponse = bookingHelper.createBooking(apiBooking);
        createdBookingIds.add(bookingResponse.getBookingId());

        assertThat(bookingResponse.getBookingId()).isNotNull();

        // Step 2: Add to Web Table UI
        webTablePage.open(ConfigProvider.getConfig().uiBaseUrl() + WEB_TABLES_URL);

        String email = uniqueFirstName.toLowerCase() + "@test.com";
        webTablePage.addRecord(
                apiBooking.getFirstName(),
                apiBooking.getLastName(),
                email,
                "29",
                String.valueOf(apiBooking.getTotalPrice()),
                "Test Customer"
        );

        // Step 3: Search for the booking using API data
        webTablePage.search(uniqueFirstName);

        // Step 4: Verify search finds the record
        assertThat(webTablePage.isRecordPresent(email))
                .as("Should find booking from API using search")
                .isTrue();

        webTablePage.verifySearchResultsCount(1);

        // Step 5: Clear search to restore normal view
        webTablePage.clearSearch();
    }
}
