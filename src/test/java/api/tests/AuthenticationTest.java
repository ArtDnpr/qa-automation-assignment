package api.tests;

import api.helpers.ApiHelper;
import config.Constants.Endpoints;
import config.Constants.ErrorMessages;
import config.Constants.JsonFields;
import config.Constants.StatusCode;
import config.Constants.TestData;
import config.TestConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@DisplayName("Authentication API Tests")
class AuthenticationTest {

    private static final TestConfig CONFIG = TestConfig.getInstance();

    @Test
    @DisplayName("POST /auth with valid credentials returns a token")
    void validCredentials_returnsToken() {
        Response response = ApiHelper.baseRequest()
                .body(Map.of(
                        "username", CONFIG.getBookingUsername(),
                        "password", CONFIG.getBookingPassword()))
                .post(Endpoints.AUTH);

        assertThat(response.statusCode()).isEqualTo(StatusCode.OK);
        String token = response.jsonPath().getString(JsonFields.TOKEN);
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("POST /auth with invalid credentials returns an error")
    void invalidCredentials_returnsError() {
        Response response = ApiHelper.baseRequest()
                .body(Map.of(
                        "username", TestData.INVALID_CREDENTIALS_USERNAME,
                        "password", TestData.INVALID_CREDENTIALS_PASSWORD))
                .post(Endpoints.AUTH);

        assertThat(response.statusCode()).isEqualTo(StatusCode.OK);
        String reason = response.jsonPath().getString(JsonFields.REASON);
        assertThat(reason).isEqualTo(ErrorMessages.BAD_CREDENTIALS);
    }
}
