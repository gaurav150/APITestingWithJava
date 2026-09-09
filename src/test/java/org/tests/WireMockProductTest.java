package org.tests;

import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;


import static io.restassured.RestAssured.*;

public class WireMockProductTest {
    String baseUrl= getWireMockBaseUrl();

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
                exception.getMessage().contains("Connection reset")
                        || exception.getMessage().contains("failed to respond"),
                "Unexpected exception: " + exception.getMessage()
        );
        System.out.println(exception.getClass().getName());

    }

    @Test(description = "testing for fault of empty response")
    public void faultEmptyResponseTestcase() {
        Exception exception = Assert.expectThrows(Exception.class, () -> {
            given()
                    .baseUri(baseUrl)
                    .when()
                    .get("/getting/empty/response");
        });

        Assert.assertTrue(exception.getMessage().contains("failed to respond"),
                "Unexpected exception: " + exception.getMessage());


    }

//
    @Test(description = "fault Testing for malformed chunk")
    public void faultMalformedChunk() {
        Exception exception = Assert.expectThrows(Exception.class, () -> {
            given()
                    .baseUri(baseUrl)
                    .when()
                    .get("/getting/malformed-chunk");
        });
        Assert.assertTrue(exception.getMessage().contains("status code: 200"),
                "Unexpected exception: " + exception.getMessage());
    }
//
    @Test(description = "fault Testing for random data close")
    public void faultRandomData() {
        Exception exception = Assert.expectThrows(Exception.class, () -> {
            given()
                    .baseUri(baseUrl)
                    .when()
                    .get("/getting/random-data-close");
        });

        Assert.assertEquals(exception.getClass(), org.apache.http.client.ClientProtocolException.class,
                "Unexpected exception type: " + exception.getClass().getName());
        Assert.assertNull(exception.getMessage(),
                "Message is not null"+exception.getMessage());
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

        assertThat(response.getStatusLine(),
                Matchers.containsString("Created"));
    }

    private String getWireMockBaseUrl() {
        String resolvedUrl = System.getenv("WIREMOCK_BASE_URL");

        if (resolvedUrl == null || resolvedUrl.isBlank()) {
            resolvedUrl = "http://localhost:8181";
        }

        System.out.println("WireMock URL = " + resolvedUrl);

        return resolvedUrl;
    }
}
