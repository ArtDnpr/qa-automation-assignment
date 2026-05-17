package api.tests;

import api.helpers.GraphQLHelper;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@DisplayName("GraphQL API Tests")
class GraphQLTest {

    @Nested
    @DisplayName("Positive Scenarios")
    class PositiveScenarios {

        @Test
        @DisplayName("Query a list with pagination/limit")
        void queryListWithPagination() {
            String query = """
                    {
                      characters(page: 1) {
                        info {
                          count
                          pages
                        }
                        results {
                          id
                          name
                          status
                        }
                      }
                    }
                    """;

            Response response = GraphQLHelper.query(query);

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.jsonPath().getInt("data.characters.info.count")).isPositive();
            List<?> results = response.jsonPath().getList("data.characters.results");
            assertThat(results).isNotEmpty().hasSizeLessThanOrEqualTo(20);
        }

        @Test
        @DisplayName("Query a single entity by ID")
        void querySingleEntityById() {
            String query = """
                    {
                      character(id: "1") {
                        id
                        name
                        status
                        species
                      }
                    }
                    """;

            Response response = GraphQLHelper.query(query);

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.jsonPath().getString("data.character.id")).isEqualTo("1");
            assertThat(response.jsonPath().getString("data.character.name")).isNotBlank();
            assertThat(response.jsonPath().getString("data.character.species")).isNotBlank();
        }

        @Test
        @DisplayName("Query using GraphQL variables")
        void queryWithVariables() {
            String query = """
                    query GetCharacter($id: ID!) {
                      character(id: $id) {
                        id
                        name
                        status
                        species
                      }
                    }
                    """;

            Response response = GraphQLHelper.query(query, Map.of("id", "2"));

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.jsonPath().getString("data.character.id")).isEqualTo("2");
            assertThat(response.jsonPath().getString("data.character.name")).isNotBlank();
        }

        @Test
        @DisplayName("Query using fragments and nested fields")
        void queryWithFragmentsAndNestedFields() {
            String query = """
                    fragment charInfo on Character {
                      name
                      status
                      species
                    }
                    
                    {
                      characters(page: 1) {
                        results {
                          ...charInfo
                          origin {
                            name
                            dimension
                          }
                          location {
                            name
                            type
                          }
                          episode {
                            name
                            air_date
                          }
                        }
                      }
                    }
                    """;

            Response response = GraphQLHelper.query(query);

            assertThat(response.statusCode()).isEqualTo(200);

            List<?> results = response.jsonPath().getList("data.characters.results");
            assertThat(results).isNotEmpty();

            assertThat(response.jsonPath().getString("data.characters.results[0].name")).isNotBlank();
            assertThat(response.jsonPath().getString("data.characters.results[0].origin.name")).isNotNull();
            assertThat(response.jsonPath().getList("data.characters.results[0].episode")).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Negative Scenarios")
    class NegativeScenarios {

        @Test
        @DisplayName("Query with invalid (non-existent) ID returns null")
        void queryWithInvalidId() {
            String query = """
                    {
                      character(id: "999999") {
                        id
                        name
                      }
                    }
                    """;

            Response response = GraphQLHelper.query(query);

            assertThat(response.statusCode()).isEqualTo(200);
            Object character = response.jsonPath().getJsonObject("data.character");
            assertThat(character).isNull();
        }

        @Test
        @DisplayName("Malformed query returns errors")
        void malformedQueryReturnsErrors() {
            String query = "{ characters(page: 1 { results { name } } }";

            Response response = GraphQLHelper.query(query);

            assertThat(response.statusCode()).isEqualTo(400);
        }

        @Test
        @DisplayName("Non-existent field returns errors")
        void nonExistentFieldReturnsErrors() {
            String query = """
                    {
                      characters(page: 1) {
                        results {
                          completely_fake_field
                        }
                      }
                    }
                    """;

            Response response = GraphQLHelper.query(query);

            assertThat(response.statusCode()).isEqualTo(400);
        }
    }
}
