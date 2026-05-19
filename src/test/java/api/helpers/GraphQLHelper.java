package api.helpers;

import config.TestConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class GraphQLHelper {

    private static final TestConfig CONFIG = TestConfig.getInstance();

    public static RequestSpecification baseRequest() {
        return RestAssured.given()
                .baseUri(CONFIG.getGraphqlBaseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(new AllureRestAssured())
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter());
    }

    public static Response query(String graphqlQuery) {
        return baseRequest()
                .body(Map.of("query", graphqlQuery))
                .post();
    }

    public static Response query(String graphqlQuery, Map<String, Object> variables) {
        return baseRequest()
                .body(Map.of("query", graphqlQuery, "variables", variables))
                .post();
    }
}
