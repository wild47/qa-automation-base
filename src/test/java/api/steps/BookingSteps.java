package api.steps;

import api.models.Booking;
import api.models.BookingResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static api.helpers.ApiHelper.getBaseSpec;
import static api.steps.AuthenticateSteps.getAuthSpec;
import static io.restassured.RestAssured.given;

public class BookingSteps {

    private static final String BOOKING_ENDPOINT = "/booking";

    @Step("Create booking")
    public BookingResponse createBooking(Booking booking) {
        Response response = given()
                .spec(getBaseSpec())
                .body(booking)
                .when()
                .post(BOOKING_ENDPOINT);

        return response.then()
                .statusCode(200)
                .extract()
                .as(BookingResponse.class);
    }

    @Step("Get booking by ID: {bookingId}")
    public Response getBooking(Integer bookingId) {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(BOOKING_ENDPOINT + "/" + bookingId);
    }

    @Step("Get all bookings")
    public Response getAllBookings() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(BOOKING_ENDPOINT);
    }

    @Step("Get bookings by firstname: {firstname}")
    public Response getBookingsByFirstname(String firstname) {
        return given()
                .spec(getBaseSpec())
                .queryParam("firstname", firstname)
                .when()
                .get(BOOKING_ENDPOINT);
    }

    @Step("Get bookings by lastname: {lastname}")
    public Response getBookingsByLastname(String lastname) {
        return given()
                .spec(getBaseSpec())
                .queryParam("lastname", lastname)
                .when()
                .get(BOOKING_ENDPOINT);
    }

    @Step("Get bookings by check-in date: {checkin}")
    public Response getBookingsByCheckin(String checkin) {
        return given()
                .spec(getBaseSpec())
                .queryParam("checkin", checkin)
                .when()
                .get(BOOKING_ENDPOINT);
    }

    @Step("Get bookings by checkout date: {checkout}")
    public Response getBookingsByCheckout(String checkout) {
        return given()
                .spec(getBaseSpec())
                .queryParam("checkout", checkout)
                .when()
                .get(BOOKING_ENDPOINT);
    }

    @Step("Update booking ID: {bookingId}")
    public Response updateBooking(Integer bookingId, Booking booking) {
        return given()
                .spec(getAuthSpec())
                .body(booking)
                .when()
                .put(BOOKING_ENDPOINT + "/" + bookingId);
    }

    @Step("Update booking ID: {bookingId} without token")
    public Response updateBookingWithoutToken(Integer bookingId, Booking booking) {
        return given()
                .spec(getBaseSpec())
                .body(booking)
                .when()
                .put(BOOKING_ENDPOINT + "/" + bookingId);
    }

    @Step("Partial update booking ID: {bookingId}")
    public Response partialUpdateBooking(Integer bookingId, Object partialBooking) {
        return given()
                .spec(getAuthSpec())
                .body(partialBooking)
                .when()
                .patch(BOOKING_ENDPOINT + "/" + bookingId);
    }

    @Step("Delete booking ID: {bookingId}")
    public Response deleteBooking(Integer bookingId) {
        return given()
                .spec(getAuthSpec())
                .when()
                .delete(BOOKING_ENDPOINT + "/" + bookingId);
    }
}
