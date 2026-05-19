package api.tests;

import api.helpers.ApiHelper;
import api.models.Booking;
import config.Constants.Endpoints;
import config.Constants.FilterParams;
import config.Constants.JsonFields;
import config.Constants.StatusCode;
import config.Constants.TestData;
import config.Constants.Validation;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@DisplayName("Booking API Tests")
@TestInstance(Lifecycle.PER_CLASS)
class BookingApiTest {

    private String authToken;

    @BeforeAll
    void authenticate() {
        authToken = ApiHelper.getAuthToken();
    }

    @Nested
    @DisplayName("CRUD Operations")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(Lifecycle.PER_CLASS)
    class CrudOperations {

        private int createdBookingId;
        private Booking originalBooking;

        @Test
        @Order(1)
        @DisplayName("Create booking via POST /booking")
        void createBooking() {
            originalBooking = ApiHelper.buildDefaultBooking();
            Booking.BookingResponse response = ApiHelper.createBooking(originalBooking);

            assertThat(response.getBookingId()).isPositive();
            assertThat(response.getBooking().getFirstname()).isEqualTo(originalBooking.getFirstname());
            assertThat(response.getBooking().getLastname()).isEqualTo(originalBooking.getLastname());
            assertThat(response.getBooking().getTotalprice()).isEqualTo(originalBooking.getTotalprice());
            assertThat(response.getBooking().isDepositpaid()).isEqualTo(originalBooking.isDepositpaid());

            createdBookingId = response.getBookingId();
        }

        @Test
        @Order(2)
        @DisplayName("Retrieve booking by ID via GET /booking/{id}")
        void getBookingById() {
            Response response = ApiHelper.getBooking(createdBookingId);

            assertThat(response.statusCode()).isEqualTo(StatusCode.OK);
            assertThat(response.jsonPath().getString(JsonFields.FIRSTNAME))
                    .isEqualTo(originalBooking.getFirstname());
            assertThat(response.jsonPath().getString(JsonFields.LASTNAME))
                    .isEqualTo(originalBooking.getLastname());
            assertThat(response.jsonPath().getInt(JsonFields.TOTAL_PRICE))
                    .isEqualTo(originalBooking.getTotalprice());
        }

        @Test
        @Order(3)
        @DisplayName("Update booking via PUT /booking/{id}")
        void updateBooking() {
            Booking updated = Booking.builder()
                    .firstname(TestData.UPDATED_FIRSTNAME)
                    .lastname(TestData.UPDATED_LASTNAME)
                    .totalprice(TestData.UPDATED_TOTAL_PRICE)
                    .depositpaid(false)
                    .bookingdates(Booking.BookingDates.builder()
                            .checkin(TestData.UPDATED_CHECKIN)
                            .checkout(TestData.UPDATED_CHECKOUT)
                            .build())
                    .additionalneeds(TestData.UPDATED_ADDITIONAL_NEEDS)
                    .build();

            Response response = ApiHelper.updateBooking(createdBookingId, updated, authToken);

            assertThat(response.statusCode()).isEqualTo(StatusCode.OK);
            assertThat(response.jsonPath().getString(JsonFields.FIRSTNAME))
                    .isEqualTo(TestData.UPDATED_FIRSTNAME);
            assertThat(response.jsonPath().getString(JsonFields.LASTNAME))
                    .isEqualTo(TestData.UPDATED_LASTNAME);
            assertThat(response.jsonPath().getInt(JsonFields.TOTAL_PRICE))
                    .isEqualTo(TestData.UPDATED_TOTAL_PRICE);
            assertThat(response.jsonPath().getBoolean(JsonFields.DEPOSIT_PAID)).isFalse();
        }

        @Test
        @Order(4)
        @DisplayName("Delete booking via DELETE /booking/{id}")
        void deleteBooking() {
            Response response = ApiHelper.deleteBooking(createdBookingId, authToken);

            assertThat(response.statusCode()).isEqualTo(StatusCode.CREATED);

            Response getResponse = ApiHelper.getBooking(createdBookingId);
            assertThat(getResponse.statusCode()).isEqualTo(StatusCode.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Search & Filter")
    @TestInstance(Lifecycle.PER_CLASS)
    class SearchAndFilter {

        private int bookingId;

        @BeforeAll
        void createTestData() {
            Booking booking = ApiHelper.buildDefaultBooking();
            Booking.BookingResponse response = ApiHelper.createBooking(booking);
            bookingId = response.getBookingId();
        }

        @AfterAll
        void cleanUp() {
            ApiHelper.deleteBooking(bookingId, authToken);
        }

        @Test
        @DisplayName("Get all bookings returns a non-empty list")
        void getAllBookings() {
            Response response = ApiHelper.getAllBookings();

            assertThat(response.statusCode()).isEqualTo(StatusCode.OK);
            assertThat(response.jsonPath().getList("$")).isNotEmpty();
        }

        @Test
        @DisplayName("Filter bookings by firstname")
        void filterByFirstname() {
            Response response = ApiHelper.getBookingsFiltered(
                    FilterParams.FIRSTNAME, TestData.DEFAULT_FIRSTNAME);

            assertThat(response.statusCode()).isEqualTo(StatusCode.OK);
            assertThat(response.jsonPath().getList(JsonFields.BOOKING_ID)).isNotEmpty();
        }

        @Test
        @DisplayName("Filter bookings by checkin date")
        void filterByCheckinDate() {
            Response response = ApiHelper.getBookingsFiltered(
                    FilterParams.CHECKIN, TestData.DEFAULT_CHECKIN);

            assertThat(response.statusCode()).isEqualTo(StatusCode.OK);
            assertThat(response.jsonPath().getList("$")).isNotNull();
        }

        @Test
        @DisplayName("Filter bookings by checkout date")
        void filterByCheckoutDate() {
            Response response = ApiHelper.getBookingsFiltered(
                    FilterParams.CHECKOUT, TestData.DEFAULT_CHECKOUT);

            assertThat(response.statusCode()).isEqualTo(StatusCode.OK);
            assertThat(response.jsonPath().getList("$")).isNotNull();
        }
    }

    @Nested
    @DisplayName("Negative Tests")
    class NegativeTests {

        @Test
        @DisplayName("Update without token returns 403")
        void updateWithoutToken_returns403() {
            Booking booking = ApiHelper.buildDefaultBooking();
            Booking.BookingResponse created = ApiHelper.createBooking(booking);

            Response response = ApiHelper.baseRequest()
                    .pathParam("id", created.getBookingId())
                    .body(booking)
                    .put(Endpoints.BOOKING_BY_ID);

            assertThat(response.statusCode()).isEqualTo(StatusCode.FORBIDDEN);

            ApiHelper.deleteBooking(created.getBookingId(), authToken);
        }

        @Test
        @DisplayName("Update with invalid token returns 403")
        void updateWithInvalidToken_returns403() {
            Booking booking = ApiHelper.buildDefaultBooking();
            Booking.BookingResponse created = ApiHelper.createBooking(booking);

            Response response = ApiHelper.updateBooking(
                    created.getBookingId(), booking, TestData.INVALID_TOKEN);

            assertThat(response.statusCode()).isEqualTo(StatusCode.FORBIDDEN);

            ApiHelper.deleteBooking(created.getBookingId(), authToken);
        }

        @Test
        @DisplayName("Get non-existent booking returns 404")
        void getNonExistentBooking_returns404() {
            Response response = ApiHelper.getBooking(Integer.MAX_VALUE);

            assertThat(response.statusCode()).isEqualTo(StatusCode.NOT_FOUND);
        }

        @Test
        @DisplayName("Delete non-existent booking returns 405")
        void deleteNonExistentBooking_returnsError() {
            Response response = ApiHelper.deleteBooking(Integer.MAX_VALUE, authToken);

            assertThat(response.statusCode()).isIn(StatusCode.NOT_FOUND, StatusCode.METHOD_NOT_ALLOWED);
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    @TestInstance(Lifecycle.PER_CLASS)
    class ValidationTests {

        private int bookingId;

        @BeforeAll
        void createTestData() {
            Booking booking = ApiHelper.buildDefaultBooking();
            Booking.BookingResponse response = ApiHelper.createBooking(booking);
            bookingId = response.getBookingId();
        }

        @AfterAll
        void cleanUp() {
            ApiHelper.deleteBooking(bookingId, authToken);
        }

        @Test
        @DisplayName("Booking response matches JSON schema")
        void responseMatchesJsonSchema() {
            ApiHelper.getBooking(bookingId)
                    .then()
                    .statusCode(StatusCode.OK)
                    .body(matchesJsonSchemaInClasspath(Validation.BOOKING_SCHEMA_FILE));
        }

        @Test
        @DisplayName("Booking response contains all required fields")
        void responseContainsRequiredFields() {
            Response response = ApiHelper.getBooking(bookingId);

            assertThat(response.statusCode()).isEqualTo(StatusCode.OK);
            assertThat(response.jsonPath().getString(JsonFields.FIRSTNAME)).isNotNull();
            assertThat(response.jsonPath().getString(JsonFields.LASTNAME)).isNotNull();
            assertThat(response.jsonPath().getInt(JsonFields.TOTAL_PRICE)).isNotNull();
            assertThat(response.jsonPath().getString(JsonFields.BOOKING_DATES_CHECKIN)).isNotNull();
            assertThat(response.jsonPath().getString(JsonFields.BOOKING_DATES_CHECKOUT)).isNotNull();
        }

        @Test
        @DisplayName("Booking dates follow YYYY-MM-DD format")
        void datesFollowExpectedFormat() {
            Response response = ApiHelper.getBooking(bookingId);

            assertThat(response.statusCode()).isEqualTo(StatusCode.OK);
            String checkin = response.jsonPath().getString(JsonFields.BOOKING_DATES_CHECKIN);
            String checkout = response.jsonPath().getString(JsonFields.BOOKING_DATES_CHECKOUT);

            assertThat(checkin).matches(Validation.DATE_PATTERN);
            assertThat(checkout).matches(Validation.DATE_PATTERN);
        }
    }
}
