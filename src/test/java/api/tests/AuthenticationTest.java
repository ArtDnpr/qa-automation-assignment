package api.tests;

import api.helpers.ApiHelper;
import config.TestConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@DisplayName("Authentication API Tests")
class AuthenticationTest {

    private static final TestConfig CONFIG = TestConfig.getInstance();

    @Test
    @DisplayName("POST /auth with valid credentials returns a token")
    void validCredentials_returnsToken() {
        Response response = ApiHelper.baseRequest()
                .body(String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                        CONFIG.getBookingUsername(), CONFIG.getBookingPassword()))
                .post("/auth");

        assertThat(response.statusCode()).isEqualTo(200);
        String token = response.jsonPath().getString("token");
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("POST /auth with invalid credentials returns an error")
    void invalidCredentials_returnsError() {
        Response response = ApiHelper.baseRequest()
                .body("{\"username\":\"wronguser\",\"password\":\"wrongpass\"}")
                .post("/auth");

        assertThat(response.statusCode()).isEqualTo(200);
        String reason = response.jsonPath().getString("reason");
        assertThat(reason).isEqualTo("Bad credentials");
    }
}
