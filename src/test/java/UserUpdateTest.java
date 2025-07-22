import Steps.UserSteps;
import Steps.UserUpdateSteps;
import URL.BaseTest;
import User.LoginUser;
import User.User;
import User.UserUpdate;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserUpdateTest {

    private User user;
    private String accessToken;
    private final UserSteps userSteps = new UserSteps();
    private final UserUpdateSteps userUpdateSteps = new UserUpdateSteps();

    @BeforeEach
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        user = User.randomUser();
        userSteps.registerUser(user).statusCode(SC_OK);
        accessToken = loginAndGetToken(user);
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userSteps.deleteUser(accessToken);
        }
    }

    private String loginAndGetToken(User user) {
        LoginUser loginUser = new LoginUser(user.getEmail(), user.getPassword());
        return given()
                .baseUri(BaseTest.BASE_URL)
                .contentType(ContentType.JSON)
                .body(loginUser)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(SC_OK)
                .extract()
                .path("accessToken");
    }

    @Step
    @Test
    @DisplayName("Обновление всех полей с авторизацией")
    public void updateAllFieldsWithAuth() {
        UserUpdate updatedUser = new UserUpdate(
                "newemail" + System.currentTimeMillis() + "@mail.ru",
                "NewPassword123",
                "NewName"
        );
        userUpdateSteps.updateUser(updatedUser, accessToken)
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Step
    @Test
    @DisplayName("Обновление только имени без авторизации (должна быть ошибка)")
    public void updateNameWithoutAuth_ShouldFail() {
        UserUpdate updatedUser = new UserUpdate(null, null, "UpdatedName");
        userUpdateSteps.updateUserWithoutAuth(updatedUser)
                .statusCode(SC_UNAUTHORIZED);
    }
}