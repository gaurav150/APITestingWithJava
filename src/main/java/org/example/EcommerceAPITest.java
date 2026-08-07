package org.example;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class EcommerceAPITest {

    private static final String BASE_URI = "https://rahulshettyacademy.com";
    String token;
    String userId;

    @Test @SuppressWarnings("java:S106")
    public void loginToWebsite() {
        RequestSpecification reqLogin = given().spec(requestSpec())
                .body(getLoginCredentials());

        LoginResponsePojo responseResult = reqLogin
                .when()
                .post("/api/ecom/auth/login")
                .then()
                .extract()
                .response()
                .as(LoginResponsePojo.class);

        token = responseResult.getToken();
        userId = responseResult.getToken();
        System.out.println("Token is -> ");
        System.out.println(token);

    }

    @Test @SuppressWarnings("java:S106")
    public void createOrder() {


    }

    private static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setContentType(ContentType.JSON)
                .build();
    }

    private static LoginCredentialsPojo getLoginCredentials() {
        LoginCredentialsPojo lp = new LoginCredentialsPojo();
        lp.setUserEmail("pankajara@gmail.com");
        lp.setUserPassword("Qwerty123");
        return lp;
    }
}
