package tests;

import org.testng.annotations.Test;
import io.github.cdimascio.dotenv.Dotenv;

import static io.restassured.RestAssured.*;

public class ProxyFirstTest {
    Dotenv dotenv = Dotenv.load();



    @Test
    public void getProductsFromProxy() {
        String baseUrl = dotenv.get("WIREMOCK_BASE_URL");
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

        System.out.println("Response from Proxy Server /products:");
        System.out.println(response);
    }

    @Test
    public void getPlaceHolderAPIUsingProxy() {
        String baseUrl = dotenv.get("JSON_PLACEHOLDER_API");

        String response =
                given()
                        .baseUri(baseUrl)
                        .when()
                        .get("/posts/3")
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();
        System.out.println("response of second test case is ->");
        System.out.println(response);
    }
}
