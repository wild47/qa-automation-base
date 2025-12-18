package contract;

import api.models.Booking;
import api.models.BookingDates;
import api.models.BookingResponse;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Consumer contract tests for Booking API using PACT.
 **/
@ExtendWith(PactConsumerTestExt.class)
@Feature("Contract Testing - Consumer")
@DisplayName("Booking API Consumer Contract Tests")
@Tag("contract")
class BookingApiConsumerTest {
    @Pact(consumer = "BookingConsumer", provider = "BookingAPI")
    public V4Pact createBookingPact(PactDslWithProvider builder) {
        PactDslJsonBody requestBody = new PactDslJsonBody()
            .stringValue("firstname", "John")
            .stringValue("lastname", "Doe")
            .numberValue("totalprice", 500)
            .booleanValue("depositpaid", true)
            .object("bookingdates")
                .stringMatcher("checkin", "\\d{4}-\\d{2}-\\d{2}", "2024-01-15")
                .stringMatcher("checkout", "\\d{4}-\\d{2}-\\d{2}", "2024-01-20")
                .closeObject()
            .asBody()
            .stringValue("additionalneeds", "Breakfast");

        PactDslJsonBody responseBody = new PactDslJsonBody()
            .numberValue("bookingid", 123)
            .object("booking")
                .stringValue("firstname", "John")
                .stringValue("lastname", "Doe")
                .numberValue("totalprice", 500)
                .booleanValue("depositpaid", true)
                .object("bookingdates")
                    .stringMatcher("checkin", "\\d{4}-\\d{2}-\\d{2}", "2024-01-15")
                    .stringMatcher("checkout", "\\d{4}-\\d{2}-\\d{2}", "2024-01-20")
                    .closeObject()
                .asBody()
                .stringValue("additionalneeds", "Breakfast")
                .closeObject()
                .asBody();

        return builder
            .uponReceiving("a request to create a booking")
                .method("POST")
                .path("/booking")
                .headers(java.util.Map.of("Content-Type", "application/json"))
                .body(requestBody)
            .willRespondWith()
                .status(200)
                .headers(java.util.Map.of("Content-Type", "application/json"))
                .body(responseBody)
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createBookingPact", port = "8888")
    @DisplayName("Should create booking according to contract")
    @Description("Verify consumer can create a booking matching the expected contract")
    @Severity(SeverityLevel.CRITICAL)
    void testCreateBookingContract() {
        BookingDates dates = BookingDates.builder()
            .checkIn("2024-01-15")
            .checkOut("2024-01-20")
            .build();

        Booking booking = Booking.builder()
            .firstName("John")
            .lastName("Doe")
            .totalPrice(500)
            .depositPaid(true)
            .bookingDates(dates)
            .additionalNeeds("Breakfast")
            .build();

        BookingResponse response = given()
            .contentType(ContentType.JSON)
            .body(booking)
            .when()
            .post("http://localhost:8888/booking")
            .then()
            .statusCode(200)
            .extract()
            .as(BookingResponse.class);

        assertThat(response.getBookingId())
            .as("Booking ID should be returned")
            .isNotNull()
            .isPositive();

        assertThat(response.getBooking().getFirstName())
            .as("First name should match")
            .isEqualTo("John");

        assertThat(response.getBooking().getTotalPrice())
            .as("Total price should match")
            .isEqualTo(500);
    }

    @Pact(consumer = "BookingConsumer", provider = "BookingAPI")
    public V4Pact getBookingByIdPact(PactDslWithProvider builder) {
        PactDslJsonBody responseBody = new PactDslJsonBody()
            .stringValue("firstname", "Jane")
            .stringValue("lastname", "Smith")
            .numberValue("totalprice", 750)
            .booleanValue("depositpaid", false)
            .object("bookingdates")
                .stringMatcher("checkin", "\\d{4}-\\d{2}-\\d{2}", "2024-03-10")
                .stringMatcher("checkout", "\\d{4}-\\d{2}-\\d{2}", "2024-03-15")
                .closeObject()
            .asBody()
            .stringValue("additionalneeds", "Late checkout");

        return builder
            .uponReceiving("a request to get a booking by ID")
                .method("GET")
                .path("/booking/1")
            .willRespondWith()
                .status(200)
                .headers(java.util.Map.of("Content-Type", "application/json"))
                .body(responseBody)
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getBookingByIdPact", port = "8888")
    @DisplayName("Should get booking by ID according to contract")
    @Description("Verify consumer can retrieve a booking by ID matching the expected contract")
    @Severity(SeverityLevel.CRITICAL)
    void testGetBookingByIdContract() {
        Booking booking = given()
            .when()
            .get("http://localhost:8888/booking/1")
            .then()
            .statusCode(200)
            .extract()
            .as(Booking.class);

        assertThat(booking.getFirstName())
            .as("First name should be returned")
            .isEqualTo("Jane");

        assertThat(booking.getLastName())
            .as("Last name should be returned")
            .isEqualTo("Smith");

        assertThat(booking.getTotalPrice())
            .as("Total price should be returned")
            .isEqualTo(750);

        assertThat(booking.getDepositPaid())
            .as("Deposit paid should be false")
            .isFalse();
    }

    @Pact(consumer = "BookingConsumer", provider = "BookingAPI")
    public V4Pact getAllBookingsPact(PactDslWithProvider builder) {
        return builder
            .uponReceiving("a request to get all bookings")
                .method("GET")
                .path("/booking")
            .willRespondWith()
                .status(200)
                .headers(java.util.Map.of("Content-Type", "application/json"))
                .body("[\n" +
                    "  {\"bookingid\": 1},\n" +
                    "  {\"bookingid\": 2},\n" +
                    "  {\"bookingid\": 3}\n" +
                    "]")
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAllBookingsPact", port = "8888")
    @DisplayName("Should get all bookings according to contract")
    @Description("Verify consumer can retrieve list of booking IDs matching the expected contract")
    @Severity(SeverityLevel.NORMAL)
    void testGetAllBookingsContract() {
        String response = given()
            .when()
            .get("http://localhost:8888/booking")
            .then()
            .statusCode(200)
            .extract()
            .asString();

        assertThat(response)
            .as("Response should contain booking IDs")
            .contains("bookingid")
            .contains("1", "2", "3");
    }

    @Pact(consumer = "BookingConsumer", provider = "BookingAPI")
    public V4Pact updateBookingPact(PactDslWithProvider builder) {
        PactDslJsonBody requestBody = new PactDslJsonBody()
            .stringValue("firstname", "UpdatedName")
            .stringValue("lastname", "UpdatedLastName")
            .numberValue("totalprice", 999)
            .booleanValue("depositpaid", true)
            .object("bookingdates")
                .stringMatcher("checkin", "\\d{4}-\\d{2}-\\d{2}", "2024-05-01")
                .stringMatcher("checkout", "\\d{4}-\\d{2}-\\d{2}", "2024-05-10")
                .closeObject()
            .asBody()
            .stringValue("additionalneeds", "Updated needs");

        PactDslJsonBody responseBody = new PactDslJsonBody()
            .stringValue("firstname", "UpdatedName")
            .stringValue("lastname", "UpdatedLastName")
            .numberValue("totalprice", 999)
            .booleanValue("depositpaid", true)
            .object("bookingdates")
                .stringMatcher("checkin", "\\d{4}-\\d{2}-\\d{2}", "2024-05-01")
                .stringMatcher("checkout", "\\d{4}-\\d{2}-\\d{2}", "2024-05-10")
                .closeObject()
            .asBody()
            .stringValue("additionalneeds", "Updated needs");

        return builder
            .uponReceiving("a request to update a booking")
                .method("PUT")
                .path("/booking/1")
                .headers(java.util.Map.of(
                    "Content-Type", "application/json",
                    "Cookie", "token=abc123"
                ))
                .body(requestBody)
            .willRespondWith()
                .status(200)
                .headers(java.util.Map.of("Content-Type", "application/json"))
                .body(responseBody)
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "updateBookingPact", port = "8888")
    @DisplayName("Should update booking according to contract")
    @Description("Verify consumer can update a booking matching the expected contract")
    @Severity(SeverityLevel.CRITICAL)
    void testUpdateBookingContract() {
        BookingDates dates = BookingDates.builder()
            .checkIn("2024-05-01")
            .checkOut("2024-05-10")
            .build();

        Booking booking = Booking.builder()
            .firstName("UpdatedName")
            .lastName("UpdatedLastName")
            .totalPrice(999)
            .depositPaid(true)
            .bookingDates(dates)
            .additionalNeeds("Updated needs")
            .build();

        Booking response = given()
            .contentType(ContentType.JSON)
            .header("Cookie", "token=abc123")
            .body(booking)
            .when()
            .put("http://localhost:8888/booking/1")
            .then()
            .statusCode(200)
            .extract()
            .as(Booking.class);

        assertThat(response.getFirstName())
            .as("First name should be updated")
            .isEqualTo("UpdatedName");

        assertThat(response.getTotalPrice())
            .as("Total price should be updated")
            .isEqualTo(999);
    }

    @Pact(consumer = "BookingConsumer", provider = "BookingAPI")
    public V4Pact deleteBookingPact(PactDslWithProvider builder) {
        return builder
            .uponReceiving("a request to delete a booking")
                .method("DELETE")
                .path("/booking/1")
                .headers(java.util.Map.of("Cookie", "token=abc123"))
            .willRespondWith()
                .status(201)
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "deleteBookingPact", port = "8888")
    @DisplayName("Should delete booking according to contract")
    @Description("Verify consumer can delete a booking matching the expected contract")
    @Severity(SeverityLevel.NORMAL)
    void testDeleteBookingContract() {
        given()
            .header("Cookie", "token=abc123")
            .when()
            .delete("http://localhost:8888/booking/1")
            .then()
            .statusCode(201);
    }
}
