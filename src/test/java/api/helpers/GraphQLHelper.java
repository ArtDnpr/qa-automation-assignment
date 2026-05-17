package api.helpers;

import config.TestConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class GraphQLHelper {

    private static final TestConfig CONFIG = TestConfig.getInstance();

    public static RequestSpecification baseRequest() {
        return RestAssured.given()
                .baseUri(CONFIG.getGraphqlBaseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.ANY)
                .filter(new AllureRestAssured())
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter());
    }

    public static Response query(String graphqlQuery) {
        Map<String, Object> body = new HashMap<>();
        body.put("query", graphqlQuery);
        return baseRequest().body(body).post();
    }

    public static Response query(String graphqlQuery, Map<String, Object> variables) {
        Map<String, Object> body = new HashMap<>();
        body.put("query", graphqlQuery);
        body.put("variables", variables);
        return baseRequest().body(body).post();
    }
}
