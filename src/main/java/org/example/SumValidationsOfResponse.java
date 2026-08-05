package org.example;

import io.restassured.path.json.JsonPath;
import org.example.files.payLoad;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class SumValidationsOfResponse {

    @Test
    @SuppressWarnings("java:S106")
    public void sumOfCourses() {
        JsonPath js = new JsonPath(payLoad.newCourses());

        int purchaseAmount = js.getInt("dashboard.purchaseAmount");
        List<Map<String, Object>> courses = js.getList("courses");

        //6. Verify if Sum of all Course prices matches with Purchase Amount
        int Sum = 0;
        for (Map<String, Object> course : courses) {
            Sum += ((Integer) course.get("price") * (Integer) course.get("copies"));
        }
        System.out.println("final value of " + Sum);

        Assert.assertEquals(Sum, purchaseAmount, "purchase amount should match with actual amount");

    }
}
