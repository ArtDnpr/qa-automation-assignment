package api.tests;

import api.helpers.ApiHelper;
import api.models.Booking;
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

        @Test
        @Order(1)
        @DisplayName("Create booking via POST /booking")
        void createBooking() {
            Booking booking = ApiHelper.buildDefaultBooking();
            Booking.BookingResponse response = ApiHelper.createBooking(booking);

            assertThat(response.getBookingId()).isPositive();
            assertThat(response.getBooking().getFirstname()).isEqualTo(booking.getFirstname());
            assertThat(response.getBooking().getLastname()).isEqualTo(booking.getLastname());
            assertThat(response.getBooking().getTotalprice()).isEqualTo(booking.getTotalprice());
            assertThat(response.getBooking().isDepositpaid()).isEqualTo(booking.isDepositpaid());

            createdBookingId = response.getBookingId();
        }

        @Test
        @Order(2)
        @DisplayName("Retrieve booking by ID via GET /booking/{id}")
        void getBookingById() {
            Response response = ApiHelper.getBooking(createdBookingId);

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.jsonPath().getString("firstname")).isEqualTo("John");
            assertThat(response.jsonPath().getString("lastname")).isEqualTo("Doe");
            assertThat(response.jsonPath().getInt("totalprice")).isEqualTo(150);
        }

        @Test
        @Order(3)
        @DisplayName("Update booking via PUT /booking/{id}")
        void updateBooking() {
            Booking updated = Booking.builder()
                    .firstname("Jane")
                    .lastname("Smith")
                    .totalprice(250)
                    .depositpaid(false)
                    .bookingdates(Booking.BookingDates.builder()
                            .checkin("2025-03-01")
                            .checkout("2025-03-15")
                            .build())
                    .additionalneeds("Lunch")
                    .build();

            Response response = ApiHelper.updateBooking(createdBookingId, updated, authToken);

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.jsonPath().getString("firstname")).isEqualTo("Jane");
            assertThat(response.jsonPath().getString("lastname")).isEqualTo("Smith");
            assertThat(response.jsonPath().getInt("totalprice")).isEqualTo(250);
            assertThat(response.jsonPath().getBoolean("depositpaid")).isFalse();
        }

        @Test
        @Order(4)
        @DisplayName("Delete booking via DELETE /booking/{id}")
        void deleteBooking() {
            Response response = ApiHelper.deleteBooking(createdBookingId, authToken);

            assertThat(response.statusCode()).isEqualTo(201);

            Response getResponse = ApiHelper.getBooking(createdBookingId);
            assertThat(getResponse.statusCode()).isEqualTo(404);
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

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.jsonPath().getList("$")).isNotEmpty();
        }

        @Test
        @DisplayName("Filter bookings by firstname")
        void filterByFirstname() {
            Response response = ApiHelper.getBookingsFiltered("firstname", "John");

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.jsonPath().getList("bookingid")).isNotEmpty();
        }

        @Test
        @DisplayName("Filter bookings by checkin date")
        void filterByCheckinDate() {
            Response response = ApiHelper.getBookingsFiltered("checkin", "2025-01-01");

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.jsonPath().getList("$")).isNotNull();
        }

        @Test
        @DisplayName("Filter bookings by checkout date")
        void filterByCheckoutDate() {
            Response response = ApiHelper.getBookingsFiltered("checkout", "2025-01-10");

            assertThat(response.statusCode()).isEqualTo(200);
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
                    .put("/booking/{id}");

            assertThat(response.statusCode()).isEqualTo(403);

            ApiHelper.deleteBooking(created.getBookingId(), authToken);
        }

        @Test
        @DisplayName("Update with invalid token returns 403")
        void updateWithInvalidToken_returns403() {
            Booking booking = ApiHelper.buildDefaultBooking();
            Booking.BookingResponse created = ApiHelper.createBooking(booking);

            Response response = ApiHelper.updateBooking(
                    created.getBookingId(), booking, "invalidtoken123");

            assertThat(response.statusCode()).isEqualTo(403);

            ApiHelper.deleteBooking(created.getBookingId(), authToken);
        }

        @Test
        @DisplayName("Get non-existent booking returns 404")
        void getNonExistentBooking_returns404() {
            Response response = ApiHelper.getBooking(Integer.MAX_VALUE);

            assertThat(response.statusCode()).isEqualTo(404);
        }

        @Test
        @DisplayName("Delete non-existent booking returns 405")
        void deleteNonExistentBooking_returnsError() {
            Response response = ApiHelper.deleteBooking(Integer.MAX_VALUE, authToken);

            assertThat(response.statusCode()).isIn(404, 405);
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
                    .statusCode(200)
                    .body(matchesJsonSchemaInClasspath("booking-schema.json"));
        }

        @Test
        @DisplayName("Booking response contains all required fields")
        void responseContainsRequiredFields() {
            Response response = ApiHelper.getBooking(bookingId);

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.jsonPath().getString("firstname")).isNotNull();
            assertThat(response.jsonPath().getString("lastname")).isNotNull();
            assertThat(response.jsonPath().getInt("totalprice")).isNotNull();
            assertThat(response.jsonPath().getString("bookingdates.checkin")).isNotNull();
            assertThat(response.jsonPath().getString("bookingdates.checkout")).isNotNull();
        }

        @Test
        @DisplayName("Booking dates follow YYYY-MM-DD format")
        void datesFollowExpectedFormat() {
            Response response = ApiHelper.getBooking(bookingId);

            assertThat(response.statusCode()).isEqualTo(200);
            String checkin = response.jsonPath().getString("bookingdates.checkin");
            String checkout = response.jsonPath().getString("bookingdates.checkout");

            assertThat(checkin).matches("\\d{4}-\\d{2}-\\d{2}");
            assertThat(checkout).matches("\\d{4}-\\d{2}-\\d{2}");
        }
    }
}
