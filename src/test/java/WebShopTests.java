import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Selectors.byClassName;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;

public class WebShopTests {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com/";
        Configuration.baseUrl = "http://demowebshop.tricentis.com/";
    }

    @Test
    @DisplayName("Verify that Wishlist contains added items")
    void addToWishListWithAuthorizationTest() {
        // Get authorization cookie
        String authorizationCookie =
                given()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .basePath("login")
                        .formParam("Email", "qaguru@qa.guru")
                        .formParam("Password", "qaguru@qa.guru1")
                        .when()
                        .post()
                        .then()
                        .statusCode(302)
                        .extract()
                        .cookie("NOPCOMMERCE.AUTH");

        // Request of adding one item to wishlist
        given()
                .cookie(authorizationCookie)
                .basePath("addproducttocart/details/53/2")
                .when()
                .post()
                .then()
                .statusCode(200);

        // Open wishlist page with browser and set authorization cookie to it
        open("wishlist");
        getWebDriver().manage().addCookie(new Cookie("NOPCOMMERCE.AUTH", authorizationCookie));
        refresh();

        // Verify that wishlist contains one item
        Assertions.assertEquals(1, $$(byClassName("cart-item-row")).size());
    }
}