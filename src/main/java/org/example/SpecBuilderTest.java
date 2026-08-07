package org.example;


import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class SpecBuilderTest {

    private static final String BASE_URI = "https://rahulshettyacademy.com";

    @Test
    public void addingPlace() {

        AddPlace p = getAddPlace();
        RequestSpecification res = given()
                .spec(requestSpecBuild())
                .body(p);


        Response response = res
                .when().post("maps/api/place/add/json");
        ValidatableResponse expectedResponse = response.then().spec(responseSpecBuild());

        expectedResponse
                .body("scope", equalTo("APP"))
                .header("Server", equalTo("Apache/2.4.52 (Ubuntu)"));

        String responseOfAPI = response.then().extract().response().asPrettyString();
        JsonPath js = new JsonPath(responseOfAPI);
        String placeId = js.getString("place_id");


        /*
         * res.then().log().all().extract().response()
         * -->  this will return whole response and print them.
         * to fetch particular value of response
         * res.then().extract().path("Key")  ----> key refers to key for which we are looking for
         * */

        //update Place

        RequestSpecification updatedRequest = given().spec(requestSpecBuild())
                .body("{\n" +
                        "    \"place_id\":\"" + placeId + "\",\n" +
                        "    \"address\": \"70 summer walk , USA\",\n" +
                        "    \"key\":\"qaclick123\"\n" +
                        "}");

        Response updatedResponse = updatedRequest
                .when().put("maps/api/place/update/json");
        updatedResponse.then().assertThat().statusCode(200)
                .body("msg", equalTo("Address successfully updated"));

    }

    private AddPlace getAddPlace() {
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

    private static RequestSpecification requestSpecBuild() {
        return new RequestSpecBuilder().setBaseUri(BASE_URI)
                .addQueryParam("key", "qaclick123")
                .addHeader("Content-Type", "application/json").build();

    }

    private static ResponseSpecification responseSpecBuild() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON).build();
    }

}
