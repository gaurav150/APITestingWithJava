package org.example;


import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;


public class OAuthExampleTest {

    private static final String BASE_URI = "https://rahulshettyacademy.com/oauthapi/oauth2/resourceOwner/token";
    private static final String BASE_URI_FOR_GETTING_COURSE_DETAILS = "https://rahulshettyacademy.com/oauthapi/getCourseDetails";
    private static final String[] COURSE_TITLES = {"Selenium Webdriver Java", "Cypress", "Protractor"};

    @Test
    @SuppressWarnings({"java:S106", "java:S125"})
    public void testingOAuth() {
        String response = given()
                .formParams("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
                .formParams("client_secret", "erZOWM9g3UtwNRj340YYaK_W")
                .formParams("grant_Type", "client_credentials")
                .formParams("scope", "trust")
                .when()
                .log()
                .all()
                .post(BASE_URI).asString();

        System.out.println("Response is ->>" + response);

        JsonPath js = new JsonPath(response);
        String accessToken = js.getString("access_token");
        System.out.println("access token is " + accessToken);

        GetCoursesDetails responseCourseDetails = given()
                .baseUri(BASE_URI_FOR_GETTING_COURSE_DETAILS)
                .queryParam("access_token", accessToken)
                .log()
                .all()
                .when()
                .get()
                .then()
                .assertThat()
                .statusCode(401)
                .extract()
                .response()
                .as(GetCoursesDetails.class);

        // As this is Free API So it is Showing Status Code of 401
        // that's why I am using it to validate what i am getting status code from API
//
//        System.out.println("courses Response is ");
//        System.out.println(responseCourseDetails);
//
//        JsonPath jr1 = new JsonPath(responseCourseDetails);
//        String url = jr1.getString("url");
//        System.out.println("url is -> "+url);

        System.out.println("url is -> ");
        System.out.println(responseCourseDetails.getUrl());
        System.out.println("Course title of the first course");
        System.out.println(responseCourseDetails.getCourses().getWebAutomation().getFirst().getCourseTitle());


        int priceOfCypress = getCoursePriceForSpecificCourse(responseCourseDetails, "Cypress");
        System.out.println("price is ->" + priceOfCypress);
        System.out.println("courses title printed for webautomation");
        List<String> webAutomationCourseTitles = getCoursesNameForWebAutomation(responseCourseDetails);
        webAutomationCourseTitles.forEach(System.out::println);

        List<String> expectedCourseTitles = Arrays.stream(COURSE_TITLES).toList();
        Assert.assertTrue(
                expectedCourseTitles.stream().allMatch(webAutomationCourseTitles::contains),
                "Expected web automation course titles not found in API response"
        );
        Assert.assertEquals(
                webAutomationCourseTitles.size(),
                expectedCourseTitles.size(),
                "Web automation course count does not match expected count"
        );


    }

    /**
     * Returns the price of a course from the {@code webAutomation} category that matches the given title.
     *
     * @param response   deserialized course-details API response containing course lists
     * @param courseName exact course title to look up (e.g. {@code "Cypress"})
     * @return the price of the matching course
     * @throws RuntimeException if no course in {@code webAutomation} has the specified title
     */
    public int getCoursePriceForSpecificCourse(GetCoursesDetails response, String courseName) {
        return response.getCourses()
                .getWebAutomation()
                .stream()
                .filter(course -> courseName.equals(course.getCourseTitle()))
                .findFirst()
                .map(Course::getPrice)
                .orElseThrow(() ->
                        new RuntimeException("Course not found: " + courseName));
    }

    /**
     * Returns the titles of all courses listed under the {@code webAutomation} category.
     *
     * @param response deserialized course-details API response containing course lists
     * @return list of course titles from the {@code webAutomation} category
     */
    public List<String> getCoursesNameForWebAutomation(GetCoursesDetails response) {
        return response.getCourses()
                .getWebAutomation()
                .stream()
                .map(Course::getCourseTitle)
                .toList();
    }
}
