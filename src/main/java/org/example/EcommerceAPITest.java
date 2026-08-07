package org.example;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;

public class EcommerceAPITest {

    private static final String BASE_URI = "https://rahulshettyacademy.com";
    String token;
    String userId;
    String productId;
    String messageResponse;


    @Test
    @SuppressWarnings("java:S106")
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
        userId = responseResult.getUserId();
        System.out.println("Token is -> ");
        System.out.println(token);

    }

    @Test
    @SuppressWarnings("java:S106")
    public void addNewProduct() {
        loginToWebsite();
        RequestSpecification addProductSpecData = addProductSpec(token);

        RequestSpecification reqAddingProducts = given().spec(addProductSpecData)
                .param("productName", "qwerty")
                .param("productAddedBy", userId)
                .param("productCategory", "fashion")
                .param("productSubCategory", "shirts")
                .param("productPrice", "11789")
                .param("productDescription", "Adidas Originals")
                .param("productFor", "men")
                .multiPart("productImage", new File("//Users//gaurav//Desktop//productImage.jpeg"));

        AddProductPojoResponse responseForAddingProduct = reqAddingProducts.when()
                .post("/api/ecom/product/add-product")
                .then().extract().response().as(AddProductPojoResponse.class);

        productId = responseForAddingProduct.getProductId();
        messageResponse = responseForAddingProduct.getMessage();
        System.out.println("id is ->> " + productId);
        System.out.println("response is -> " + responseForAddingProduct);

    }


    @Test
    @SuppressWarnings("java:S106")
    public void createOrder() {

        // Add new Product
        addNewProduct();
        //Create Order
        CreateOrderDetailsInPojo cdr = new CreateOrderDetailsInPojo();
        cdr.setCountry("India");
        cdr.setProductOrderedId(productId);
        List<CreateOrderDetailsInPojo> myList = new ArrayList<>();
        myList.add(cdr);
        CreateOrderPojo cd = new CreateOrderPojo();
        cd.setOrders(myList);
        RequestSpecification createOrderRequest = given().spec(createOrder(token))
                .body(cd);

        CreateOrderPojoResponse orderResponse = createOrderRequest
                .when()
                .post("/api/ecom/order/create-order")
                .then()
                .statusCode(201)
                .extract().response().as(CreateOrderPojoResponse.class);

        String messageType = orderResponse.getMessage();
        System.out.println(messageType);


    }

    @Test
    @SuppressWarnings("java:S106")
    public void deleteProductFromUI() {
        createOrder();
        // Delete Product
        RequestSpecification deleteOrderRequest = given().spec(deleteProduct(token, productId));

        String deleteProductResponse = deleteOrderRequest
                .delete("/api/ecom/product/delete-product/{productId}")
                .asPrettyString();

        JsonPath js = new JsonPath(deleteProductResponse);
        String messageDeletedProduct = js.getString("message");
        System.out.println("message of deleted product is ->> " + messageDeletedProduct);
        System.out.println("response is " + deleteProductResponse);
        Assert.assertEquals(messageDeletedProduct, "Product Deleted Successfully");

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

    private static RequestSpecification addProductSpec(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .addHeader("Authorization", token)
                .build();
    }

    private static RequestSpecification createOrder(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", token)
                .build();
    }

    private static RequestSpecification deleteProduct(String token, String id) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", token)
                .addPathParam("productId", id)
                .build();
    }
}
