package URL;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;


public class BaseTest {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
    }
}