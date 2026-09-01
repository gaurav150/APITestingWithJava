package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.AddPlace;
import org.example.Location;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class ProxyFirstTest {
    Dotenv dotenv = Dotenv.load();

    @Test(description = "Get products from proxy server")
    public void getProductsFromProxy() {
        String baseUrl = dotenv.get("WIREMOCK_BASE_URL");
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

    @Test(description = "Get Json Holder API from proxy server")
    public void getPlaceHolderAPIUsingProxy() {
        String baseUrl = dotenv.get("JSON_PLACEHOLDER_API");

        int postId = 3;
        Response response = getJsonPlaceholderPostById(baseUrl, postId, 200);
        Assert.assertNotNull(response.getBody(), "body should not be null");
    }

    @Test(description = "Verify valid post ID returns post details")
    public void verifyValidPostId() {
        String baseUrl = dotenv.get("JSON_PLACEHOLDER_API");

        int postId = 3;
        Response response = getJsonPlaceholderPostById(baseUrl, postId, 200);
        JsonPath js = new JsonPath(response.asString());

        Assert.assertEquals(js.getInt("id"), postId, "postId should match");
        Assert.assertNotNull(response.getBody(), "body should not be null");
        System.out.println("response of second test case is ->");
        System.out.println(response.asString());
    }

    @Test(description = "Verify non-existing post ID")
    public void verifyInvalidPostId() throws JsonProcessingException {
        String baseUrl = dotenv.get("JSON_PLACEHOLDER_API");
        int postId = 99999;
        Response response = getJsonPlaceholderPostById(baseUrl, postId, 404);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.asString());

        Assert.assertTrue(jsonNode.isObject());
        Assert.assertTrue(jsonNode.isEmpty());
    }

    @Test(description = "Verify negative post ID")
    public void verifyNegativePostId() throws JsonProcessingException {
        String baseUrl = dotenv.get("JSON_PLACEHOLDER_API");
        int postId = -1;
        Response response = getJsonPlaceholderPostById(baseUrl, postId, 404);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.asString());

        Assert.assertTrue(jsonNode.isObject());
        Assert.assertTrue(jsonNode.isEmpty());
        System.out.println(response.asString());
    }

    @Test(description = "adding place using proxy API.")
    public void addingPlace() {
        String baseURI = dotenv.get("SHETTY_URL");
        AddPlace p = getAddPlace();
        RequestSpecification res = given()
                .spec(requestSpecBuild(baseURI))
                .body(p);


        Response response = res
                .when().post("maps/api/place/add/json");
        ValidatableResponse expectedResponse = response.then().spec(responseSpecBuild(200));

        expectedResponse
                .body("scope", equalTo("APP"))
                .header("Server", equalTo("Apache/2.4.52 (Ubuntu)"));

        String responseOfAPI = response.then().extract().response().asPrettyString();
        JsonPath js = new JsonPath(responseOfAPI);
        String placeId = js.getString("place_id");
        System.out.println("place_id " + placeId);


        /*
         * res.then().log().all().extract().response()
         * -->  this will return whole response and print them.
         * to fetch particular value of response
         * res.then().extract().path("Key")  ----> key refers to key for which we are looking for
         * */

        //update Place

        RequestSpecification updatedRequest = given().spec(requestSpecBuild(baseURI))
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

    //    positive test case for PUT
    @Test(description = "Verify product can be updated successfully using PUT request")
    public void verifyProductUpdateUsingPutRequest() {
        String baseUrl = dotenv.get("WIREMOCK_BASE_URL");
        String requestBody = """
                {
                "id": 1,
                "title": "Hello",
                "price": 0.1,
                "description": "First",
                "category": "Edit",
                "image": "http://example.com"
                }
                """;
        int productID = 1;
        Response response = updateProductById(baseUrl, requestBody, productID, 200);
        Assert.assertNotNull(response.getBody());
    }


    @Test(description = "Verify product can be updated with different valid product details")
    public void verifyProductUpdateWithDifferentValidDetails() {
        String baseUrl = dotenv.get("WIREMOCK_BASE_URL");

        String requestBody = """
                {
                    "id": 2,
                    "title": "Updated Product",
                    "price": 99.99,
                    "description": "Updated product description",
                    "category": "Electronics",
                    "image": "http://example.com/product.jpg"
                }
                """;

        int productID = 2;
        Response response = updateProductById(baseUrl, requestBody, productID, 200);
        Assert.assertEquals(response.jsonPath().getInt("id"), productID,
                "Product ID should match");

    }

    // negative test case for PUT
    @Test(description = "Verify update fails for non-existing product ID")
    public void verifyProductUpdateWithNonExistingProductId() {
        String baseUrl = dotenv.get("WIREMOCK_BASE_URL");

        String requestBody = """
                {
                    "id": 9999,
                    "title": "Updated Product",
                    "price": 50.0,
                    "description": "Test product",
                    "category": "Test",
                    "image": "http://example.com"
                }
                """;

        int productID = 9999;

        Response response = updateProductById(baseUrl, requestBody, productID, 400);
        Assert.assertEquals(
                response.jsonPath().getString("error"),
                "Again, you have encountered an incorrect product ID.",
                "Error message should match"
        );
    }

    @Test(description = "Verify product update fails when request body is invalid")
    public void verifyProductUpdateWithInvalidRequestBody() {
        String baseUrl = dotenv.get("WIREMOCK_BASE_URL");

        String requestBody = """
                {
                    "id": 1,
                    "title": "Updated Product",
                    "price": "invalid-price",
                    "description": "Test product"
                }
                """;

        int productID = 1;
        Response response = updateProductById(baseUrl, requestBody, productID, 400);
        Assert.assertEquals(
                response.jsonPath().getString("error"),
                "Again, you have encountered an incorrect product ID.",
                "Error message should match"
        );
    }

    // delete product id
    @Test(description = "Verify existing product can be deleted successfully")
    public void verifyDeleteExistingProduct() {

        String baseUrl = dotenv.get("WIREMOCK_BASE_URL");
        int productID = 1;

        Response response = deleteProductById(baseUrl, productID, 200);

        Assert.assertEquals(
                response.jsonPath().getInt("id"),
                productID,
                "Deleted product ID should match"
        );
    }

    @Test(description = "Verify deleted product details are returned successfully")
    public void verifyDeletedProductDetails() {

        String baseUrl = dotenv.get("WIREMOCK_BASE_URL");
        int productID = 1;

        Response response = deleteProductById(baseUrl, productID, 200);

        Assert.assertEquals(
                response.jsonPath().getInt("id"),
                productID,
                "Product ID should match"
        );

        Assert.assertNotNull(
                response.jsonPath().getString("title"),
                "Product title should not be null"
        );

        Assert.assertNotNull(
                response.jsonPath().getString("category"),
                "Product category should not be null"
        );
    }

//    Negative test cases for delete operation

    @Test(description = "Verify delete fails for non-existing product ID")
    public void verifyDeleteNonExistingProduct() {

        String baseUrl = dotenv.get("WIREMOCK_BASE_URL");
        int productID = 9999;

        Response response = deleteProductById(baseUrl, productID, 404);

        Assert.assertTrue(
                response.jsonPath().getString("error").contains("Product not found"),
                "Error message should indicate product was not found"
        );
    }

    @Test(description = "Verify delete fails for invalid product ID")
    public void verifyDeleteWithInvalidProductId() {

        String baseUrl = dotenv.get("WIREMOCK_BASE_URL");
        int productID = -9999;

        Response response = deleteProductById(baseUrl, productID, 400);

        Assert.assertTrue(
                response.jsonPath()
                        .getString("error")
                        .contains("Invalid product ID"),
                "Error message should indicate invalid product ID"
        );
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

    private static RequestSpecification requestSpecBuildForPlaceHolderAPI(String baseURI, int postId) {
        return new RequestSpecBuilder()
                .setBaseUri(baseURI)
                .addPathParam("postId", postId)
                .build();
    }

    private static RequestSpecification requestSpecBuild(String baseURI) {
        return new RequestSpecBuilder().setBaseUri(baseURI)
                .addQueryParam("key", "qaclick123")
                .addHeader("Content-Type", "application/json").build();

    }

    private static ResponseSpecification responseSpecBuild(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .expectContentType(ContentType.JSON).build();
    }

    private static Response getJsonPlaceholderPostById(String baseUrl, int postId, int statusCode) {
        return given()
                .log()
                .all()
                .spec(requestSpecBuildForPlaceHolderAPI(baseUrl, postId))
                .when()
                .get("/posts/{postId}")
                .then()
                .spec(responseSpecBuild(statusCode))
                .extract()
                .response();
    }

    private Response updateProductById(String baseUrl, String requestBody, int productID, int statusCode) {
        return given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .pathParam("productId", productID)
                .baseUri(baseUrl)
                .when()
                .put("/products/{productId}")
                .then()
                .statusCode(statusCode)
                .extract()
                .response();
    }

    private Response deleteProductById(String baseUrl, int productID, int statusCode) {
        return given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .pathParam("productId", productID)
                .baseUri(baseUrl)
                .when()
                .delete("/products/{productId}")
                .then()
                .statusCode(statusCode)
                .extract()
                .response();
    }
}
