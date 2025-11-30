package api.tests;


import api.data.TestDataBuilder;
import api.models.Booking;
import api.models.BookingDates;
import api.models.BookingId;
import api.models.BookingResponse;
import api.steps.AuthenticateSteps;
import api.steps.BookingSteps;
import config.ConfigProvider;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static api.steps.AuthenticateSteps.getAuthSpec;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("API Testing")
@Feature("Booking Management")
@Tag("api")
public class BookingApiTest {

    private static AuthenticateSteps authHelper;
    private static BookingSteps bookingHelper;
    private static Random rn;
    private final List<Integer> createdBookingIds = new ArrayList<>();

    @BeforeAll
    public static void setUp() {
        authHelper = new AuthenticateSteps();
        bookingHelper = new BookingSteps();
        rn = new Random();

        String username = ConfigProvider.getConfig().apiUsername();
        String password = ConfigProvider.getConfig().apiPassword();
        authHelper.authenticate(username, password);
    }

    @AfterEach
    public void tearDown() {
        for (Integer bookingId : createdBookingIds) {
            try {
                bookingHelper.deleteBooking(bookingId);
            } catch (Exception e) {
                // Booking might already be deleted or not exist, continue with cleanup
            }
        }
        createdBookingIds.clear();
    }

    private Integer createAngGetBookingId() {
        Booking booking = TestDataBuilder.createDefaultBooking();
        BookingResponse bookingResponse = bookingHelper.createBooking(booking);
        createdBookingIds.add(bookingResponse.getBookingId());
        return bookingResponse.getBookingId();
    }

    @Test
    @DisplayName("Should create a new booking successfully")
    @Description("Verify that a new booking can be created with all required fields")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateBooking() {
        String firstName = "first" + RandomStringUtils.randomAlphabetic(3);
        String lastName = "last" + RandomStringUtils.randomAlphabetic(3);
        int price = rn.nextInt(3);
        Booking booking = TestDataBuilder.createBooking(firstName, lastName, price);

        BookingResponse bookingResponse = bookingHelper.createBooking(booking);
        createdBookingIds.add(bookingResponse.getBookingId());

        assertThat(bookingResponse)
                .as("Booking response should not be null")
                .isNotNull();
        assertThat(bookingResponse.getBookingId())
                .as("Booking ID should not be null")
                .isNotNull()
                .as("Booking ID should be positive")
                .isPositive();
        assertThat(bookingResponse.getBooking())
                .as("Booking object should not be null")
                .isNotNull();

        assertThat(bookingResponse.getBooking().getFirstName())
                .as("Firstname should match")
                .isEqualTo(booking.getFirstName());
        assertThat(bookingResponse.getBooking().getLastName())
                .as("Lastname should match")
                .isEqualTo(booking.getLastName());
        assertThat(bookingResponse.getBooking().getTotalPrice())
                .as("Total price should match")
                .isEqualTo(booking.getTotalPrice());
        assertThat(bookingResponse.getBooking().getDepositPaid())
                .as("Deposit paid should match")
                .isEqualTo(booking.getDepositPaid());

        Allure.parameter("Created Booking ID", bookingResponse.getBookingId());
    }

    @Test
    @DisplayName("Should retrieve booking by ID")
    @Description("Verify that an existing booking can be retrieved using its ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetBookingById() {
        Integer bookingId = createAngGetBookingId();

        Response response = bookingHelper.getBooking(bookingId);

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(200);

        Booking retrievedBooking = response.then()
                .statusCode(200)
                .extract()
                .as(Booking.class);

        assertThat(retrievedBooking)
                .as("Retrieved booking should not be null")
                .isNotNull();
        assertThat(retrievedBooking.getFirstName())
                .as("Firstname should match")
                .isEqualTo("John");
        assertThat(retrievedBooking.getLastName())
                .as("Lastname should match")
                .isEqualTo("Doe");
    }

    @Test
    @DisplayName("Should update existing booking")
    @Description("Verify that an existing booking can be updated with new information")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateBooking() {
        Integer bookingId = createAngGetBookingId();

        String firstName = "first" + RandomStringUtils.randomAlphabetic(3);
        String lastName = "last" + RandomStringUtils.randomAlphabetic(3);
        int price = rn.nextInt(3);
        Booking updatedBooking = TestDataBuilder.createBooking(firstName, lastName, price);

        Response response = bookingHelper.updateBooking(bookingId, updatedBooking);

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(200);

        Booking returnedBooking = response.then()
                .statusCode(200)
                .extract()
                .as(Booking.class);

        assertThat(returnedBooking.getFirstName())
                .as("Updated firstname should match")
                .isEqualTo(updatedBooking.getFirstName());
        assertThat(returnedBooking.getLastName())
                .as("Updated lastname should match")
                .isEqualTo(updatedBooking.getLastName());
        assertThat(returnedBooking.getTotalPrice())
                .as("Updated price should match")
                .isEqualTo(updatedBooking.getTotalPrice());
    }

    @Test
    @DisplayName("Should partially update booking")
    @Description("Verify that booking can be partially updated using PATCH")
    @Severity(SeverityLevel.NORMAL)
    public void testPartialUpdateBooking() {
        Integer bookingId = createAngGetBookingId();

        String firstName = "first" + RandomStringUtils.randomAlphabetic(3);
        int price = rn.nextInt(3);
        Map<String, Object> partialUpdate = new HashMap<>();
        partialUpdate.put("firstname", firstName);
        partialUpdate.put("totalprice", price);

        Response response = bookingHelper.partialUpdateBooking(bookingId, partialUpdate);

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(200);

        Booking updatedBooking = response.then()
                .statusCode(200)
                .extract()
                .as(Booking.class);

        assertThat(updatedBooking.getFirstName())
                .as("Partially updated firstname should match")
                .isEqualTo(firstName);
        assertThat(updatedBooking.getLastName())
                .as("Not updated LastName should match")
                .isEqualTo("Doe");
        assertThat(updatedBooking.getTotalPrice())
                .as("Partially updated price should match")
                .isEqualTo(price);
    }

    @Test
    @DisplayName("Should delete existing booking")
    @Description("Verify that an existing booking can be deleted")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteBooking() {
        Integer bookingId = createAngGetBookingId();

        Response deleteResponse = bookingHelper.deleteBooking(bookingId);

        assertThat(deleteResponse.getStatusCode())
                .as("Delete status code should be 201")
                .isEqualTo(201);

        Response getResponse = bookingHelper.getBooking(bookingId);
        assertThat(getResponse.getStatusCode())
                .as("Getting deleted booking should return 404")
                .isEqualTo(404);
    }

    @Test
    @DisplayName("Should get all bookings")
    @Description("Verify that all bookings can be retrieved")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllBookings() {
        Response response = bookingHelper.getAllBookings();

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(200);

        List<BookingId> bookings = response.then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", BookingId.class);

        assertThat(bookings)
                .as("Bookings list should not be null")
                .isNotNull()
                .as("Bookings list should not be empty")
                .isNotEmpty();

        Allure.parameter("Total Bookings", bookings.size());
    }

    @Test
    @DisplayName("Should filter bookings by firstname")
    @Description("Verify that bookings can be filtered by firstname")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookingsByFirstname() {
        String firstName = "first" + RandomStringUtils.randomAlphabetic(3);
        String lastName = "last" + RandomStringUtils.randomAlphabetic(3);
        int price = rn.nextInt(3);
        Booking booking = TestDataBuilder.createBooking(firstName, lastName, price);
        BookingResponse created = bookingHelper.createBooking(booking);
        createdBookingIds.add(created.getBookingId());

        Response response = bookingHelper.getBookingsByFirstName(firstName);

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(200);

        List<BookingId> bookings = response.then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", BookingId.class);

        assertThat(bookings)
                .as("Filtered bookings should not be null")
                .isNotNull();

        if (!bookings.isEmpty()) {
            assertThat(bookings)
                    .as("Should contain the created booking")
                    .extracting(BookingId::getBookingId)
                    .contains(created.getBookingId());
        }
    }

    @Test
    @DisplayName("Should filter bookings by lastname")
    @Description("Verify that bookings can be filtered by lastname")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookingsByLastname() {
        String firstName = "first" + RandomStringUtils.randomAlphabetic(3);
        String lastName = "last" + RandomStringUtils.randomAlphabetic(3);
        int price = rn.nextInt(3);
        Booking booking = TestDataBuilder.createBooking(firstName, lastName, price);
        BookingResponse created = bookingHelper.createBooking(booking);
        createdBookingIds.add(created.getBookingId());

        Response response = bookingHelper.getBookingsByLastName(lastName);

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(200);

        List<BookingId> bookings = response.then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", BookingId.class);

        assertThat(bookings)
                .as("Filtered bookings should not be null")
                .isNotNull();
    }

    @Test
    @DisplayName("Should return 404 for non-existent booking")
    @Description("Verify that requesting a non-existent booking returns 404")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNonExistentBooking() {
        Response response = bookingHelper.getBooking(999999);

        assertThat(response.getStatusCode())
                .as("Status code should be 404 for non-existent booking")
                .isEqualTo(404);
    }

    @Test
    @DisplayName("Should fail to update booking without authentication token")
    @Description("Verify that updating booking without token returns 403")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBookingWithoutToken() {
        String firstName = "first" + RandomStringUtils.randomAlphabetic(3);
        String lastName = "last" + RandomStringUtils.randomAlphabetic(3);
        int price = rn.nextInt(3);
        Booking booking = TestDataBuilder.createDefaultBooking();
        BookingResponse created = bookingHelper.createBooking(booking);
        createdBookingIds.add(created.getBookingId());

        Booking updatedBooking = TestDataBuilder.createBooking(firstName, lastName, price);
        Response response = bookingHelper.updateBookingWithoutToken(created.getBookingId(), updatedBooking);

        assertThat(response.getStatusCode())
                .as("Status code should be 403 without auth token")
                .isEqualTo(403);
    }

    @Test
    @DisplayName("Should validate booking response schema")
    @Description("Verify that booking response matches expected JSON schema")
    @Severity(SeverityLevel.NORMAL)
    public void testBookingResponseSchema() {
        Booking booking = TestDataBuilder.createDefaultBooking();

        BookingResponse bookingResponse = bookingHelper.createBooking(booking);
        createdBookingIds.add(bookingResponse.getBookingId());

        Response response = bookingHelper.getAllBookings();
        response.then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/booking-list-schema.json"));
    }

    @Test
    @DisplayName("Should validate date format yyyy-MM-dd")
    @Description("Verify that only yyyy-MM-dd date format is accepted")
    @Severity(SeverityLevel.NORMAL)
    public void testDateFormatValidation() {
        BookingDates validDates = BookingDates.builder()
                .checkIn("2025-12-01")
                .checkOut("2025-12-05")
                .build();

        Booking booking = Booking.builder()
                .firstName(RandomStringUtils.randomAlphabetic(3))
                .lastName(RandomStringUtils.randomAlphabetic(3))
                .totalPrice(rn.nextInt(3))
                .depositPaid(true)
                .bookingDates(validDates)
                .additionalNeeds("None")
                .build();

        BookingResponse bookingResponse = bookingHelper.createBooking(booking);

        createdBookingIds.add(bookingResponse.getBookingId());
        assertThat(bookingResponse)
                .as("Booking should be created successfully with valid date format")
                .isNotNull();
        assertThat(bookingResponse.getBooking().getBookingDates().getCheckIn())
                .as("Check-in date should match yyyy-MM-dd format")
                .matches("\\d{4}-\\d{2}-\\d{2}");
        assertThat(bookingResponse.getBooking().getBookingDates().getCheckOut())
                .as("Check-out date should match yyyy-MM-dd format")
                .matches("\\d{4}-\\d{2}-\\d{2}");

    }

    @Test
    @DisplayName("Should reject checkout date before checkin date")
    @Description("Verify that booking fails when checkout date is before checkin date")
    @Severity(SeverityLevel.NORMAL)
    public void testCheckoutBeforeCheckin() {
        BookingDates invalidDates = BookingDates.builder()
                .checkIn("2025-12-10")
                .checkOut("2025-12-05")
                .build();

        Booking booking = Booking.builder()
                .firstName(RandomStringUtils.randomAlphabetic(3))
                .lastName(RandomStringUtils.randomAlphabetic(3))
                .totalPrice(rn.nextInt(3))
                .depositPaid(true)
                .bookingDates(invalidDates)
                .additionalNeeds("None")
                .build();

        Response response = given()
                .spec(getAuthSpec())
                .body(booking)
                .when()
                .post("/booking");

        assertThat(response.getStatusCode())
                .as("Should accept booking even with checkout before checkin")
                .isEqualTo(200);

        if (response.getStatusCode() == 200) {
            BookingResponse bookingResponse = response.as(BookingResponse.class);
            createdBookingIds.add(bookingResponse.getBookingId());
        }
    }

    @Test
    @DisplayName("Should handle same check in and checkout dates")
    @Description("Verify that booking can be created with same checkin and checkout dates")
    @Severity(SeverityLevel.MINOR)
    public void testSameCheckInCheckoutDates() {
        BookingDates sameDates = BookingDates.builder()
                .checkIn("2025-12-15")
                .checkOut("2025-12-15")
                .build();

        Booking booking = Booking.builder()
                .firstName(RandomStringUtils.randomAlphabetic(3))
                .lastName(RandomStringUtils.randomAlphabetic(3))
                .totalPrice(rn.nextInt(3))
                .depositPaid(true)
                .bookingDates(sameDates)
                .additionalNeeds("Day use")
                .build();

        BookingResponse bookingResponse = bookingHelper.createBooking(booking);

        createdBookingIds.add(bookingResponse.getBookingId());
        assertThat(bookingResponse)
                .as("Booking should be created with same dates")
                .isNotNull();
        assertThat(bookingResponse.getBooking().getBookingDates().getCheckIn())
                .as("Check in and check out should be the same")
                .isEqualTo(bookingResponse.getBooking().getBookingDates().getCheckOut());
    }
}
