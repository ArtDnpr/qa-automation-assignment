package config;

import lombok.Getter;

@Getter
public final class TestConfig {

    private static final TestConfig INSTANCE = new TestConfig();

    private final String restfulBookerBaseUrl;
    private final String graphqlBaseUrl;
    private final String demoQaBaseUrl;
    private final String bookingUsername;
    private final String bookingPassword;

    private TestConfig() {
        restfulBookerBaseUrl = resolveProperty("restful.booker.base.url",
                "https://restful-booker.herokuapp.com");
        graphqlBaseUrl = resolveProperty("graphql.base.url",
                "https://spacex-production.up.railway.app/graphql");
        demoQaBaseUrl = resolveProperty("demoqa.base.url",
                "https://demoqa.com");
        bookingUsername = resolveProperty("booking.username", "admin");
        bookingPassword = resolveProperty("booking.password", "password123");
    }

    public static TestConfig getInstance() {
        return INSTANCE;
    }

    private String resolveProperty(String key, String defaultValue) {
        String sysProperty = System.getProperty(key);
        if (sysProperty != null) {
            return sysProperty;
        }
        String envVar = System.getenv(key.replace('.', '_').toUpperCase());
        if (envVar != null) {
            return envVar;
        }
        return defaultValue;
    }
}
