package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import io.github.cdimascio.dotenv.Dotenv;


import static io.restassured.RestAssured.given;

public class WireMockProductTest {
    Dotenv dotenv = Dotenv.load();
    String baseUrl = dotenv.get("WIREMOCK_BASE_URL");

    @Test
    public void getProductsFromWireMock() {

        System.out.println("value of url is --> " + baseUrl);
        String response =
                        given()
                        .baseUri(baseUrl)
                        .when()
                        .get("/products")
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        System.out.println("Response from WireMock /products:");
        System.out.println(response);
    }


    @Test(expectedExceptions = Exception.class)
    public void faultTestCase() {
                given()
                .baseUri(baseUrl)
                .when()
                .get("/getting/network/fault");

    }

    @Test
    public void faultSecondTestcase() {
        Exception exception = Assert.expectThrows(Exception.class, () -> {
                    given()
                    .baseUri(baseUrl)
                    .when()
                    .get("/getting/network/fault");
        });

        System.out.println("exception message is -->>>>");
        System.out.println(exception);
        Assert.assertTrue(
                exception.getMessage().contains("failed to respond"));

    }
}
