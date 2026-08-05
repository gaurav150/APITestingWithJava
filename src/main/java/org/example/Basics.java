package org.example;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.example.files.payLoad;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Basics {

    @SuppressWarnings("java:S6126")
    public static void main(String[] args) {

        // validate if Add Place API is working  as expected

        // rest assured API working on given. when. then.

        // given - all input details
        // when - submit the API - resource and http method
        // then - validate the response is working as expected

        // https://rahulshettyacademy.com/maps/api/place/update/json?key=qaclick123
        RestAssured.baseURI = "https://rahulshettyacademy.com";
        Response res = given()
                .log()
                .all()
                .queryParam("key", "qaclick123")
                .header("Content-Type", "application/json")
                .body(payLoad.AddPlace())
                .when().post("maps/api/place/add/json");

        res.then().log().all().assertThat().statusCode(200)
                .body("scope", equalTo("APP"))
                .header("Server", equalTo("Apache/2.4.52 (Ubuntu)"));

        String responseOfAPI = res.then().extract().response().asPrettyString();
        JsonPath js = new JsonPath(responseOfAPI);
        String placeId = js.getString("place_id");


        /*
         * res.then().log().all().extract().response()
         * -->  this will return whole response and print them.
         * to fetch particular value of response
         * res.then().extract().path("Key")  ----> key refers to key for which we are looking for
         * */

        //update Place

        Response updatedResponse = given().log().all()
                .queryParam("key", "qaclick123")
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"place_id\":\"" + placeId + "\",\n" +
                        "    \"address\": \"70 summer walk , USA\",\n" +
                        "    \"key\":\"qaclick123\"\n" +
                        "}")
                .when().put("maps/api/place/update/json");
        updatedResponse.then().assertThat().statusCode(200)
                .body("msg", equalTo("Address successfully updated"));

//        String responseOfAPIUpdate = updatedResponse.jsonPath().getString("msg");

    }
}
