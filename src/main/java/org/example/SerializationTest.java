package org.example;


import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class SerializationTest {

    private static final String BASE_URI = "https://rahulshettyacademy.com";

    @Test
    public void addingPlace() {

        AddPlace p = getAddPlace();
        Response res = given()
                .baseUri(BASE_URI)
                .log()
                .all()
                .queryParam("key", "qaclick123")
                .header("Content-Type", "application/json")
                .body(p)
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

    }

    private static AddPlace getAddPlace() {
        AddPlace p = new AddPlace();
        p.setAccuracy(50);
        p.setAddress("29, side layout, cohen 09");
        p.setLanguage("French-IN");
        p.setPhone_number("(+91) 983 893 3937");
        p.setWebsite("http://google.com");
        p.setName("FrontLine House");
        List<String> myList = new ArrayList<>();
        myList.add("shoe park");
        myList.add("shop");
        p.setTypes(myList);
        Location l = new Location();
        l.setLat(-38.383494);
        l.setLng(33.427362);
        p.setLocation(l);
        return p;
    }

}
