package api.tests;


import api.data.TestDataBuilder;
import api.models.Booking;
import api.models.BookingId;
import api.models.BookingResponse;
import api.steps.AuthenticateSteps;
import api.steps.BookingSteps;
import config.ConfigProvider;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("API Testing")
@Feature("Booking Management")
@Tag("api")
public class BookingApiTest {

    private static AuthenticateSteps authHelper;
    private static BookingSteps bookingHelper;
    private static Random rn;

    @BeforeAll
    public static void setUp() {
        authHelper = new AuthenticateSteps();
        bookingHelper = new BookingSteps();
        rn = new Random();

        String username = ConfigProvider.getConfig().apiUsername();
        String password = ConfigProvider.getConfig().apiPassword();
        authHelper.authenticate(username, password);
    }

    private Integer createAngGetBookingId() {
        Booking booking = TestDataBuilder.createDefaultBooking();
        BookingResponse bookingResponse = bookingHelper.createBooking(booking);
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

        bookingHelper.deleteBooking(bookingResponse.getBookingId());
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

        bookingHelper.deleteBooking(bookingId);
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

        bookingHelper.deleteBooking(bookingId);
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

        bookingHelper.deleteBooking(bookingId);
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

        bookingHelper.deleteBooking(created.getBookingId());
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

        bookingHelper.deleteBooking(created.getBookingId());
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

        Booking updatedBooking = TestDataBuilder.createBooking(firstName, lastName, price);
        Response response = bookingHelper.updateBookingWithoutToken(created.getBookingId(), updatedBooking);

        assertThat(response.getStatusCode())
                .as("Status code should be 403 without auth token")
                .isEqualTo(403);

        bookingHelper.deleteBooking(created.getBookingId());
    }

    @Test
    @DisplayName("Should validate booking response schema")
    @Description("Verify that booking response matches expected JSON schema")
    @Severity(SeverityLevel.NORMAL)
    public void testBookingResponseSchema() {
        Booking booking = TestDataBuilder.createDefaultBooking();

        bookingHelper.createBooking(booking);

        Response response = bookingHelper.getAllBookings();
        response.then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/booking-list-schema.json"));
    }
}
