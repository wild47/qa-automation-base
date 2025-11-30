package api.tests;

import api.models.AuthRequest;
import api.steps.AuthenticateSteps;
import config.ConfigProvider;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("API Testing")
@Feature("Authentication")
@Tag("api")
public class AuthenticationTest {

    private AuthenticateSteps authHelper;

    @BeforeEach
    public void setUp() {
        authHelper = new AuthenticateSteps();
    }

    @Test
    @DisplayName("Should successfully authenticate with valid credentials")
    @Description("Verify that authentication endpoint returns a valid token with correct credentials")
    @Severity(SeverityLevel.CRITICAL)
    public void testSuccessfulAuthentication() {
        String username = ConfigProvider.getConfig().apiUsername();
        String password = ConfigProvider.getConfig().apiPassword();

        String token = authHelper.authenticate(username, password);

        assertThat(token)
                .as("Auth token should not be null")
                .isNotNull();
        assertThat(token)
                .as("Auth token should not be empty")
                .isNotEmpty();
        assertThat(token.length())
                .as("Auth token should have reasonable length")
                .isEqualTo(15);
    }

    @Test
    @DisplayName("Should fail authentication with invalid username")
    @Description("Verify that authentication fails with incorrect username")
    @Severity(SeverityLevel.CRITICAL)
    public void testAuthenticationWithInvalidUsername() {
        AuthRequest authRequest = AuthRequest.builder()
                .username("invaliduser")
                .password(ConfigProvider.getConfig().apiPassword())
                .build();

        Response response = authHelper.postAuth(authRequest);

        assertThat(response.getStatusCode())
                .as("Status code should be 200 even for failed auth")
                .isEqualTo(200);

        String responseBody = response.getBody().asString();
        assertThat(responseBody)
                .as("Response should indicate authentication failure")
                .contains("Bad credentials");
    }

    @Test
    @DisplayName("Should fail authentication with invalid password")
    @Description("Verify that authentication fails with incorrect password")
    @Severity(SeverityLevel.CRITICAL)
    public void testAuthenticationWithInvalidPassword() {
        AuthRequest authRequest = AuthRequest.builder()
                .username(ConfigProvider.getConfig().apiUsername())
                .password("wrongpassword")
                .build();

        Response response = authHelper.postAuth(authRequest);

        assertThat(response.getStatusCode())
                .as("Status code should be 200 even for failed auth")
                .isEqualTo(200);

        String responseBody = response.getBody().asString();
        assertThat(responseBody)
                .as("Response should indicate authentication failure")
                .contains("Bad credentials");
    }

    @Test
    @DisplayName("Should fail authentication with empty credentials")
    @Description("Verify that authentication fails when credentials are empty")
    @Severity(SeverityLevel.NORMAL)
    public void testAuthenticationWithEmptyCredentials() {
        AuthRequest authRequest = AuthRequest.builder()
                .username("")
                .password("")
                .build();

        Response response = authHelper.postAuth(authRequest);

        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(200);

        String responseBody = response.getBody().asString();
        assertThat(responseBody)
                .as("Response should indicate authentication failure")
                .contains("Bad credentials");
    }
}
