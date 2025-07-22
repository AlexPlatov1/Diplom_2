package Steps;

import URL.BaseTest;
import User.UserUpdate;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserUpdateSteps {
    public final String UPDATE_USER = "/api/auth/user";

    @Step("Обновление данных пользователя с авторизацией")
    public ValidatableResponse updateUser(UserUpdate user, String accessToken) {
        return given()
                .baseUri(BaseTest.BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(UPDATE_USER)
                .then();
    }

    @Step("Обновление данных пользователя без авторизации")
    public ValidatableResponse updateUserWithoutAuth(UserUpdate user) {
        return given()
                .baseUri(BaseTest.BASE_URL)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .patch(UPDATE_USER)
                .then();
    }
}