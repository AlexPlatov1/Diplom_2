import Order.Order;
import Steps.OrderStep;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

public class OrderTest {

    private OrderStep orderSteps;
    private UserSteps userSteps;
    private User user;
    private String accessToken;
    private List<String> validIngredients = new ArrayList<>();
    private List<String> invalidIngredients = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        orderSteps = new OrderStep();
        userSteps = new UserSteps();
        user = User.randomUser();
        userSteps.registerUser(user);
        LoginUser loginUser = new LoginUser(user.getEmail(), user.getPassword());
        Response loginResponse = userSteps.loginUser(loginUser).extract().response();
        accessToken = loginResponse.path("accessToken");
        // Понял вроде так что надо взять список ингридиентов, а не встааить готовый
        Response ingredientsResponse = RestAssured.given().get("https://stellarburgers.nomoreparties.site/api/ingredients");
        ingredientsResponse.then().statusCode(SC_OK);
        List<String> ingredientsIds = ingredientsResponse.jsonPath().getList("data._id");
        validIngredients.clear();
        validIngredients.add(ingredientsIds.get(0));
        validIngredients.add(ingredientsIds.get(1));
        invalidIngredients.add("11111111111111111111");
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Step
    @Test
    @DisplayName("создание заказа с авторизацией и валидными ингредиентами")
    public void createOrderWithAuthAndValidIngredientsTest() {
        Order order = new Order(validIngredients);
        orderSteps.createOrder(order, accessToken)
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("order.number", notNullValue())
                .body("order.ingredients", hasSize(validIngredients.size()))
                .body("order.ingredients[0]._id", equalTo(validIngredients.get(0)))
                .body("order.ingredients[1]._id", equalTo(validIngredients.get(1)));
    }

    @Step
    @Test
    @DisplayName("Создание заказа без авторизации и с валидными ингредиентами")
    public void createOrderWithoutAuthTest() {
        Order order = new Order(validIngredients);
        orderSteps.createOrder(order, "")
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("order.ingredients", not(empty()));
    }

    @Step
    @Test
    @DisplayName("создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        Order order = new Order(Collections.emptyList());
        orderSteps.createOrder(order, accessToken)
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step
    @Test
    @DisplayName("создание заказа с неверными ингредиентами")
    public void createOrderWithInvalidIngredientsTest() {
        Order order = new Order(invalidIngredients);
        orderSteps.createOrder(order, accessToken)
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Step
    @Test
    @DisplayName("Создание заказа без авторизации и с неверными ингредиентами")
    public void createOrderWithoutAuthAndInvalidIngredientsTest() {
        Order order = new Order(invalidIngredients);
        orderSteps.createOrder(order, "")
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Step
    @Test
    @DisplayName("Получить список заказов текущего пользователя")
    public void getCurrentUserOrdersTest() {
        orderSteps.getOrders(accessToken)
                .statusCode(SC_OK)
                .body("success", Matchers.is(true))
                .body("orders", Matchers.notNullValue());
    }

    @Step
    @Test
    @DisplayName("Получить список заказов без авторизации")
    public void getOrdersWithoutAuthTest() {
        orderSteps.getOrders("")
                .statusCode(SC_UNAUTHORIZED)
                .body("success", Matchers.is(false))
                .body("message", equalTo("You should be authorised"));
    }
}