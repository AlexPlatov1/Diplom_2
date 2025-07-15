import Steps.UserSteps;
import User.User;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserTest {
    private User user;
    private final UserSteps userSteps = new UserSteps();
    private String accessToken;

    @BeforeEach
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        user = User.randomUser();
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Step
    @Test
    @DisplayName("Создание пользователя")
    public void createNewUserTest() {
        userSteps.registerUser(user)
                .statusCode(SC_OK)
                .body("success", Matchers.is (true));
    }

    @Step
    @Test
    @DisplayName("создать пользователя, который уже зарегистрирован")
    public void createAreadyExistsUserTest() {
        userSteps.registerUser(user);
        userSteps.registerUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("User already exists"));
    }

    @Step
    @Test
    @DisplayName("создать пользователя и не заполнить одно из обязательных полей. Имя")
    public void createUserWhithoutNameTest () {
        user.setName(null);
        userSteps.registerUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Step
    @Test
    @DisplayName("создать пользователя и не заполнить одно из обязательных полей. Пароль")
    public void createUserWhithoutPasswordTest () {
        user.setPassword(null);
        userSteps.registerUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Step
    @Test
    @DisplayName("создать пользователя и не заполнить одно из обязательных полей. Email")
    public void createUserWhithoutEmailTest () {
        user.setEmail(null);
        userSteps.registerUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

}