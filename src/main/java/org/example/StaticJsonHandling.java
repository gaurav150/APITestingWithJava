package org.example;


import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.example.files.payLoad;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.*;

@SuppressWarnings("java:S115")
public class StaticJsonHandling {

    private static final String BASE_URI = "http://216.10.245.166";
    private static final String filePath = "/Users/gaurav/Documents/workspace/abc_testing.json";

    @Test
    @SuppressWarnings("java:S106")
    public void addBook() throws IOException {

        Response resp = given().
                baseUri(BASE_URI).
                header("Content-Type", "application/json").
                body(generateStringFromResource(filePath)).
                when().
                post("/Library/Addbook.php").
                then().
                assertThat().statusCode(200).extract().response();

        JsonPath js = new JsonPath(resp.asString());

        String id = js.getString("ID");
        System.out.println(" id is ->" + id);

        // Delete Book

        Response delResp = given()
                .baseUri(BASE_URI)
                .header("Content-Type", "application/json")
                .body(payLoad.DeleteBook(id))
                .delete("/Library/DeleteBook.php")
                .then().assertThat().statusCode(200).extract().response();

        System.out.println("response is ->" + delResp.asString());

        JsonPath jd = new JsonPath(delResp.asString());
        String actualMsg = jd.getString("msg");
        Assert.assertEquals(actualMsg, "book is successfully deleted");


    }


    public static String generateStringFromResource(String path) throws IOException {

        return new String(Files.readAllBytes(Paths.get(path)));
    }

}
