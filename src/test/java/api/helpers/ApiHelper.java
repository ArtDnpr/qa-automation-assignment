package api.helpers;

import api.models.Booking;
import config.Constants.Endpoints;
import config.Constants.StatusCode;
import config.Constants.TestData;
import config.TestConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

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
                .body(Map.of(
                        "username", CONFIG.getBookingUsername(),
                        "password", CONFIG.getBookingPassword()))
                .post(Endpoints.AUTH)
                .then()
                .statusCode(StatusCode.OK)
                .extract()
                .path("token");
    }

    public static Booking.BookingResponse createBooking(Booking booking) {
        return baseRequest()
                .body(booking)
                .post(Endpoints.BOOKING)
                .then()
                .statusCode(StatusCode.OK)
                .extract()
                .as(Booking.BookingResponse.class);
    }

    public static Response getBooking(int bookingId) {
        return baseRequest()
                .pathParam("id", bookingId)
                .get(Endpoints.BOOKING_BY_ID);
    }

    public static Response updateBooking(int bookingId, Booking booking, String token) {
        return baseRequest()
                .cookie("token", token)
                .pathParam("id", bookingId)
                .body(booking)
                .put(Endpoints.BOOKING_BY_ID);
    }

    public static Response deleteBooking(int bookingId, String token) {
        return baseRequest()
                .cookie("token", token)
                .pathParam("id", bookingId)
                .delete(Endpoints.BOOKING_BY_ID);
    }

    public static Response getAllBookings() {
        return baseRequest()
                .get(Endpoints.BOOKING);
    }

    public static Response getBookingsFiltered(String paramName, String paramValue) {
        return baseRequest()
                .queryParam(paramName, paramValue)
                .get(Endpoints.BOOKING);
    }

    public static Booking buildDefaultBooking() {
        return Booking.builder()
                .firstname(TestData.DEFAULT_FIRSTNAME)
                .lastname(TestData.DEFAULT_LASTNAME)
                .totalprice(TestData.DEFAULT_TOTAL_PRICE)
                .depositpaid(TestData.DEFAULT_DEPOSIT_PAID)
                .bookingdates(Booking.BookingDates.builder()
                        .checkin(TestData.DEFAULT_CHECKIN)
                        .checkout(TestData.DEFAULT_CHECKOUT)
                        .build())
                .additionalneeds(TestData.DEFAULT_ADDITIONAL_NEEDS)
                .build();
    }
}
