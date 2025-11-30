package api.steps;

import api.models.AuthRequest;
import api.models.AuthResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import static api.helpers.ApiHelper.getBaseSpec;
import static io.restassured.RestAssured.given;

@Slf4j
public class AuthenticateSteps {

    private static final String AUTH_ENDPOINT = "/auth";
    private static String authToken;

    @Step("Authenticate user")
    public String authenticate(String username, String password) {
        log.info("Authenticating user: {}", username);
        AuthRequest authRequest = AuthRequest.builder()
                .username(username)
                .password(password)
                .build();

        Response response = given()
                .spec(getBaseSpec())
                .body(authRequest)
                .when()
                .post(AUTH_ENDPOINT);

        AuthResponse authResponse = response.then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class);

        authToken = authResponse.getToken();
        log.info("Received token: {}", authToken);
        return authToken;
    }

    public static RequestSpecification getAuthSpec() {
        return getBaseSpec()
                .cookie("token", authToken);
    }

    @Step("Post authentication request")
    public Response postAuth(AuthRequest authRequest) {
        return given()
                .spec(getBaseSpec())
                .body(authRequest)
                .when()
                .post(AUTH_ENDPOINT);
    }
}
