package config;

public final class Constants {

    private Constants() {
    }

    public static final class StatusCode {
        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int BAD_REQUEST = 400;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int METHOD_NOT_ALLOWED = 405;

        private StatusCode() {
        }
    }

    public static final class Endpoints {
        public static final String AUTH = "/auth";
        public static final String BOOKING = "/booking";
        public static final String BOOKING_BY_ID = "/booking/{id}";

        private Endpoints() {
        }
    }

    public static final class PagePaths {
        public static final String PRACTICE_FORM = "/automation-practice-form";
        public static final String WEB_TABLES = "/webtables";

        private PagePaths() {
        }
    }

    public static final class JsonFields {
        public static final String FIRSTNAME = "firstname";
        public static final String LASTNAME = "lastname";
        public static final String TOTAL_PRICE = "totalprice";
        public static final String DEPOSIT_PAID = "depositpaid";
        public static final String BOOKING_DATES_CHECKIN = "bookingdates.checkin";
        public static final String BOOKING_DATES_CHECKOUT = "bookingdates.checkout";
        public static final String TOKEN = "token";
        public static final String REASON = "reason";
        public static final String BOOKING_ID = "bookingid";

        private JsonFields() {
        }
    }

    public static final class FilterParams {
        public static final String FIRSTNAME = "firstname";
        public static final String CHECKIN = "checkin";
        public static final String CHECKOUT = "checkout";

        private FilterParams() {
        }
    }

    public static final class TestData {
        public static final String DEFAULT_FIRSTNAME = "John";
        public static final String DEFAULT_LASTNAME = "Doe";
        public static final int DEFAULT_TOTAL_PRICE = 150;
        public static final boolean DEFAULT_DEPOSIT_PAID = true;
        public static final String DEFAULT_CHECKIN = "2025-01-01";
        public static final String DEFAULT_CHECKOUT = "2025-01-10";
        public static final String DEFAULT_ADDITIONAL_NEEDS = "Breakfast";

        public static final String UPDATED_FIRSTNAME = "Jane";
        public static final String UPDATED_LASTNAME = "Smith";
        public static final int UPDATED_TOTAL_PRICE = 250;
        public static final String UPDATED_CHECKIN = "2025-03-01";
        public static final String UPDATED_CHECKOUT = "2025-03-15";
        public static final String UPDATED_ADDITIONAL_NEEDS = "Lunch";

        public static final String INVALID_TOKEN = "invalidtoken123";
        public static final String INVALID_CREDENTIALS_USERNAME = "wronguser";
        public static final String INVALID_CREDENTIALS_PASSWORD = "wrongpass";

        private TestData() {
        }
    }

    public static final class ErrorMessages {
        public static final String BAD_CREDENTIALS = "Bad credentials";

        private ErrorMessages() {
        }
    }

    public static final class Validation {
        public static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";
        public static final String BOOKING_SCHEMA_FILE = "booking-schema.json";

        private Validation() {
        }
    }

    public static final class WebTableColumns {
        public static final int FIRST_NAME = 0;
        public static final int LAST_NAME = 1;
        public static final int AGE = 2;
        public static final int EMAIL = 3;
        public static final int SALARY = 4;
        public static final int DEPARTMENT = 5;

        private WebTableColumns() {
        }
    }
}
