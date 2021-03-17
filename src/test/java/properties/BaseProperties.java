package properties;

import java.nio.file.Paths;

public interface BaseProperties {
    String BASE_URI = "https://api.1up.health/";
    String AUTH_BASE_URI = "https://auth.1up.health/";
    String AUTH_CODE_END_POINT = "user-management/v1/user/auth-code";
    String AUTH_TOKEN_END_POINT = "oauth2/token";

    String REPORT_CONFIG_PATH= Paths.get("src/test/java").toAbsolutePath().toString() + "/configs/extent-config.xml";
}
