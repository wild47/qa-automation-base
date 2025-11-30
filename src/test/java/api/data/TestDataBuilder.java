package api.data;


import api.models.Booking;
import api.models.BookingDates;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestDataBuilder {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Booking createDefaultBooking() {
        return Booking.builder()
                .firstName("John")
                .lastName("Doe")
                .totalPrice(150)
                .depositPaid(true)
                .bookingDates(BookingDates.builder()
                        .checkIn(LocalDate.now().plusDays(1).format(DATE_FORMATTER))
                        .checkOut(LocalDate.now().plusDays(5).format(DATE_FORMATTER))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
    }

    public static Booking createBooking(String firstname, String lastname, Integer price) {
        return Booking.builder()
                .firstName(firstname)
                .lastName(lastname)
                .totalPrice(price)
                .depositPaid(true)
                .bookingDates(BookingDates.builder()
                        .checkIn(LocalDate.now().plusDays(1).format(DATE_FORMATTER))
                        .checkOut(LocalDate.now().plusDays(5).format(DATE_FORMATTER))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
    }
}