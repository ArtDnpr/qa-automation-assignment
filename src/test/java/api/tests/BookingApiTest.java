package api.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("api")
@DisplayName("Booking API Tests")
class BookingApiTest {

    // TODO: Iteration 2 — implement booking CRUD, search/filter, negative, validation tests

    @Nested
    @DisplayName("CRUD Operations")
    class CrudOperations {
        // - Create booking (POST /booking)
        // - Retrieve booking by ID (GET /booking/{id})
        // - Update booking (PUT /booking/{id})
        // - Delete booking (DELETE /booking/{id})
    }

    @Nested
    @DisplayName("Search & Filter")
    class SearchAndFilter {
        // - Get all bookings (GET /booking)
        // - Filter by name (GET /booking?firstname=John)
        // - Filter by check-in / check-out dates
    }

    @Nested
    @DisplayName("Negative Tests")
    class NegativeTests {
        // - Invalid authentication
        // - Update without token
        // - Non-existent booking
        // - Invalid date formats
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {
        // - Response schema validation
        // - Required fields validation
        // - Date format validation
    }
}
