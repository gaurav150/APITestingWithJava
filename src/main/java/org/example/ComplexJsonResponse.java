package org.example;


import io.restassured.path.json.JsonPath;
import org.example.files.payLoad;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

public class ComplexJsonResponse {

    @SuppressWarnings("java:S106")
    public static void main(String[] args) {
        JsonPath js = new JsonPath(payLoad.newCourses());

        // print number of courses retuned by API

        int courseCount = js.getInt("courses.size()");
        System.out.println("total number of courses are -> " + courseCount);

        // print purchase amount

        int purchaseAmount = js.getInt("dashboard.purchaseAmount");
        System.out.println("purchase amount " + purchaseAmount);

        // 3. Print Title of the first course
        //

        String titleOfFirstCourse = js.getString("courses[0].title");
        System.out.println("title is -> " + titleOfFirstCourse);

        //4. Print All course titles and their respective Prices
        //
        List<String> titles = js.getList("courses.title");
        for (int i = 0; i < titles.size(); i++) {
            System.out.println(i + ": " + titles.get(i));
        }

        List<Map<String, Object>> courses = js.getList("courses");
        for (Map<String, Object> course : courses) {
            System.out.println("Title: " + course.get("title"));
            System.out.println("Price: " + course.get("price"));
            System.out.println("Copies: " + course.get("copies"));
            System.out.println("-----");
        }

//        for (String course : titles) {
//            System.out.println(course);}

        //5. Print no of copies sold by RPA Course
        //
        for (Map<String, Object> course : courses) {
            if (course.get("title").equals("RPA")) {
                System.out.println("number of copies are -->> " + course.get("copies"));
                break;
            }

        }

        //6. Verify if Sum of all Course prices matches with Purchase Amount
        int Sum = 0;
        for (Map<String, Object> course : courses) {
            Sum += ((Integer) course.get("price") * (Integer) course.get("copies"));
        }
        System.out.println("final value of " + Sum);

        Assert.assertEquals(Sum, purchaseAmount, "purchase amount should match with actual amount");


    }
}
