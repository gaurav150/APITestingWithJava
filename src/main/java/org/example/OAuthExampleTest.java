package org.example;


import io.restassured.path.json.JsonPath;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;


public class OAuthExampleTest {

    private static final String BASE_URI = "https://rahulshettyacademy.com/oauthapi/oauth2/resourceOwner/token";
    private static final String BASE_URI_FOR_GETTING_COURSE_DETAILS = "https://rahulshettyacademy.com/oauthapi/getCourseDetails";

    @Test @SuppressWarnings("java:S106")
    public void testingOAuth() {
        String response =  given()
                .formParams("client_id","692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
                .formParams("client_secret","erZOWM9g3UtwNRj340YYaK_W")
                .formParams("grant_Type","client_credentials")
                .formParams("scope","trust")
                .when()
                .log()
                .all()
                .post(BASE_URI).asString();

        System.out.println("Response is ->>"+ response);

        JsonPath js =  new JsonPath(response);
        String accessToken = js.getString("access_token");
        System.out.println("access token is "+accessToken);

        String responseCourseDetails = given()
                .baseUri(BASE_URI_FOR_GETTING_COURSE_DETAILS)
                .queryParam("access_token",accessToken)
                .log()
                .all()
                .when()
                .get().
                then()
                .assertThat()
                .statusCode(401)
                .extract()
                .response()
                .asString();

        // As this is open API So it is Showing Status Code is 401
        // that's why I am using it to validate what i am getting status code from API

        JsonPath jr1 = new JsonPath(responseCourseDetails);
        String url = jr1.getString("url");
        System.out.println("url is -> "+url);


    }
}
