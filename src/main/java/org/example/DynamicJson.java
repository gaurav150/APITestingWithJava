package org.example;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.example.files.payLoad;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class DynamicJson {

    private static final String BASE_URI = "http://216.10.245.166";

    @Test(dataProvider = "BooksData")
    @SuppressWarnings("java:S106")
    public void addBook(String isbn, String aisle) {
        Response response = given()
                .baseUri(BASE_URI)
                .header("Content-Type", "application/json")
                .body(payLoad.AddBook(isbn, aisle))
                .when()
                .post("/Library/Addbook.php")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        String msg = response.jsonPath().getString("Msg");
        String id = response.jsonPath().getString("ID");

        System.out.println("message " + msg);
        System.out.println("id " + id);
        Assert.assertEquals(msg, "successfully added");
        Assert.assertNotNull(id);

        // DeleteBooks
        Response responseDel = given()
                .baseUri(BASE_URI)
                .header("Content-Type", "application/json")
                .body(payLoad.DeleteBook(id))
                .when()
                .delete("/Library/DeleteBook.php")
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .response();

        String fullDeleteResponse = responseDel.asString();
        System.out.println("full delete response: " + fullDeleteResponse);

        JsonPath jd = new JsonPath(fullDeleteResponse);
        String deleteMsg = jd.getString("msg");
        System.out.println("delete message: " + deleteMsg);
        Assert.assertEquals(deleteMsg,"book is successfully deleted");
    }

    @DataProvider(name = "BooksData")
    public static Object[][] getData() {
        return new Object[][]{{"abcvaqaq", "123445"}, {"qwerqaz", "987664"}, {"tyurffq", "4321221"}};
    }
}
