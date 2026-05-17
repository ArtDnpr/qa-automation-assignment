package api.helpers;

import api.models.Booking;
import config.TestConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiHelper {

    private static final TestConfig CONFIG = TestConfig.getInstance();

    public static RequestSpecification baseRequest() {
        return RestAssured.given()
                .baseUri(CONFIG.getRestfulBookerBaseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.ANY)
                .filter(new AllureRestAssured())
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter());
    }

    public static String getAuthToken() {
        return baseRequest()
                .body(String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                        CONFIG.getBookingUsername(), CONFIG.getBookingPassword()))
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    public static Booking.BookingResponse createBooking(Booking booking) {
        return baseRequest()
                .body(booking)
                .post("/booking")
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.BookingResponse.class);
    }

    public static Response getBooking(int bookingId) {
        return baseRequest()
                .pathParam("id", bookingId)
                .get("/booking/{id}");
    }

    public static Response updateBooking(int bookingId, Booking booking, String token) {
        return baseRequest()
                .cookie("token", token)
                .pathParam("id", bookingId)
                .body(booking)
                .put("/booking/{id}");
    }

    public static Response deleteBooking(int bookingId, String token) {
        return baseRequest()
                .cookie("token", token)
                .pathParam("id", bookingId)
                .delete("/booking/{id}");
    }

    public static Response getAllBookings() {
        return baseRequest()
                .get("/booking");
    }

    public static Response getBookingsFiltered(String paramName, String paramValue) {
        return baseRequest()
                .queryParam(paramName, paramValue)
                .get("/booking");
    }

    public static Booking buildDefaultBooking() {
        return Booking.builder()
                .firstname("John")
                .lastname("Doe")
                .totalprice(150)
                .depositpaid(true)
                .bookingdates(Booking.BookingDates.builder()
                        .checkin("2025-01-01")
                        .checkout("2025-01-10")
                        .build())
                .additionalneeds("Breakfast")
                .build();
    }
}
