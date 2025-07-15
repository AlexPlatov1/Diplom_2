import Steps.UserSteps;
import User.LoginUser;
import User.User;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest {
    private User user;
    private final UserSteps userSteps = new UserSteps();
    private String accessToken;

    @BeforeEach
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        user = User.randomUser();
        userSteps.registerUser(user);
        Response response = userSteps.loginUser(new LoginUser(user.getEmail(), user.getPassword())).extract().response();
        accessToken = response.path("accessToken");
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Step
    @Test
    @DisplayName("Успешная авторизация пользователя")
    public void successfulLoginTest() {
        userSteps.loginUser(new LoginUser(user.getEmail(), user.getPassword()))
                .statusCode(SC_OK)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Step
    @Test
    @DisplayName("Ошибка авторизации при неправильном пароле")
    public void invalidPasswordLoginTest() {
        LoginUser invalidUser = LoginUser.getUserCredentialsWithInvalidPassword(new LoginUser(user.getEmail(), user.getPassword()));
        userSteps.loginUser(invalidUser)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", Matchers.is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step
    @Test
    @DisplayName("Ошибка авторизации при неправильном адресе электронной почты")
    public void invalidEmailLoginTest() {
        LoginUser invalidUser = LoginUser.getUserCredentialsWithInvalidLogin(new LoginUser(user.getEmail(), user.getPassword()));
        userSteps.loginUser(invalidUser)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", Matchers.is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step
    @Test
    @DisplayName("Ошибка авторизации при отсутствии пароля")
    public void missingPasswordLoginTest() {
        LoginUser noPasswordUser = LoginUser.getUserCredentialsWithoutPassword(new LoginUser(user.getEmail(), user.getPassword()));
        userSteps.loginUser(noPasswordUser)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", Matchers.is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step
    @Test
    @DisplayName("Ошибка авторизации при отсутствии email")
    public void missingEmailLoginTest() {
        LoginUser noEmailUser = LoginUser.getUserCredentialsWithoutLogin(new LoginUser(user.getEmail(), user.getPassword()));
        userSteps.loginUser(noEmailUser)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", Matchers.is(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}