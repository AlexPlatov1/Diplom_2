package Steps;

import Order.Order;
import URL.BaseTest;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderStep {
    public final String CREATE_ORDER = "/api/orders";
    public final String GET_ORDERS = "/api/orders";

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order, String accessToken) {
        return given()
                .baseUri(BaseTest.BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(CREATE_ORDER)
                .then();
    }

    @Step("Получение заказов авторизованного пользователя")
    public ValidatableResponse getOrders(String accessToken) {
        return given()
                .baseUri(BaseTest.BASE_URL)
                .header("Authorization", accessToken)
                .when()
                .get(GET_ORDERS)
                .then();
    }

    @Step("Получение заказов неавторизованного пользователя")
    public ValidatableResponse getOrdersWithoutAuth() {
        return given()
                .baseUri(BaseTest.BASE_URL)
                .when()
                .get(GET_ORDERS)
                .then();
    }
}