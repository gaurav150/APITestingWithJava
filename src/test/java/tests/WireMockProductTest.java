package tests;

import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.github.cdimascio.dotenv.Dotenv;
import static org.hamcrest.MatcherAssert.assertThat;


import static io.restassured.RestAssured.*;

public class WireMockProductTest {
    Dotenv dotenv = Dotenv.load();
    String baseUrl = dotenv.get("WIREMOCK_BASE_URL");
//    String baseUrlFakeStore = dotenv.get("PRODUCTS_TARGET_URL");
//    above URI for Real API

    @Test(description = "getting product from stubbing files.")
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


    @Test(expectedExceptions = Exception.class, description = "testing for fault tests")
    public void faultTestCase() {
        given()
                .baseUri(baseUrl)
                .when()
                .get("/getting/network/fault");

    }

    @Test(description = "testing for fault tests 2nd method")
    public void faultSecondTestcase() {
        Exception exception = Assert.expectThrows(Exception.class, () -> {
            given()
                    .baseUri(baseUrl)
                    .when()
                    .get("/getting/network/fault");
        });

        Assert.assertTrue(
                exception.getMessage().contains("failed to respond"));

    }

    @Test(description = "Adding a new product to fakeStoreAPI using stub")
    public void postAddNewProduct() {
        String requestBody = """
                {
                    "id": 0,
                    "title": "Sample",
                    "price": 0.9,
                    "description": "Hello sample",
                    "category": "fun",
                    "image": "http://example.com"
                }
                """;
        Response response = given()
                .header("ContentType", "application/json")
                .body(requestBody)
                .baseUri(baseUrl)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .extract()
                .response();
        System.out.println(response.asString());
        System.out.println(response.getStatusLine());

        assertThat(response.getStatusLine(),
                Matchers.containsString("Created"));
    }
}
