package Steps;

import URL.BaseTest;
import User.LoginUser;
import User.User;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserSteps {
    public final String REGISTRATION = "/api/auth/register";
    public final String LOGIN = "/api/auth/login";
    public final String DELETE_USER = "/api/auth/user";

    @Step("Регистрация пользователя")
    public ValidatableResponse registerUser(User registrationUser) {
        return given()
                .baseUri(BaseTest.BASE_URL)
                .contentType(ContentType.JSON)
                .body(registrationUser)
                .when()
                .post(REGISTRATION)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(LoginUser loginUser) {
        return given()
                .baseUri(BaseTest.BASE_URL)
                .contentType(ContentType.JSON)
                .body(loginUser)
                .when()
                .post(LOGIN)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .baseUri(BaseTest.BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_USER)
                .then();
    }

}

