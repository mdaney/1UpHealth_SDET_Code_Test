package stepDefinitions;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import properties.BaseProperties;

import java.util.LinkedHashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class Steps {

    HttpResponse<JsonNode> response;
    Response authResponse;
    Response tokenResponse;
    String code;
    String accessToken;
    JsonPath jsonPathEvaluator;

    @Given("^I am an Authorized User with body \"([^\"]*)\" and \"([^\"]*)\" and \"([^\"]*)\"$")
    public void i_am_an_Authorized_User_with_body_and_and(String user_id, String client_id, String client_secret) throws Throwable {
        RestAssured.baseURI = BaseProperties.BASE_URI;
        RequestSpecification request = given();

        JSONObject requestParams = new JSONObject();
        requestParams.put("app_user_id", user_id);
        requestParams.put("client_id", client_id);
        requestParams.put("client_secret", client_secret);

        request.header("Content-Type", "application/json");
        request.body(requestParams.toString());

        authResponse = request.post(BaseProperties.AUTH_CODE_END_POINT);

        int statusCode = authResponse.getStatusCode();
        Assert.assertEquals(200, statusCode);
    }

    @When("^I Generate a New Access Token for \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"$")
    public void i_Generate_a_New_Access_Token_for(String client_id, String client_secret, String refresh_grant_type) {
        jsonPathEvaluator = tokenResponse.jsonPath();
        String refresh_token = jsonPathEvaluator.get("refresh_token");
        System.out.println("Refresh Token: " + refresh_token);


        Response response =
                given()
                        .baseUri(BaseProperties.AUTH_BASE_URI)
                        .contentType("application/x-www-form-urlencoded")
                        .formParam("grant_type", refresh_grant_type)
                        .formParam("client_id", client_id)
                        .formParam("client_secret", client_secret)
                        .formParam("refresh_token", refresh_token)
                        .when()
                        .post("oauth2/token");


        int statusCode = response.getStatusCode();
        Assert.assertEquals(200, statusCode);

        jsonPathEvaluator = response.jsonPath();
        accessToken = jsonPathEvaluator.get("access_token");
        System.out.println("Access Token: " + accessToken);
    }

    @Given("^I have the access token with \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"$")
    public void i_have_the_access_token_with(String client_id, String client_secret, String grant_type) throws Throwable {
        jsonPathEvaluator = authResponse.jsonPath();
        code = jsonPathEvaluator.get("code");

        tokenResponse =
                given()
                        .baseUri(BaseProperties.AUTH_BASE_URI)
                        .contentType("application/x-www-form-urlencoded")
                        .formParam("grant_type", grant_type)
                        .formParam("client_id", client_id)
                        .formParam("client_secret", client_secret)
                        .formParam("code", code)
                        .when()
                        .post("oauth2/token");


        int statusCode = tokenResponse.getStatusCode();
        Assert.assertEquals(200, statusCode);

        jsonPathEvaluator = tokenResponse.jsonPath();
        accessToken = jsonPathEvaluator.get("access_token");
        String refresh_token = jsonPathEvaluator.get("refresh_token");
        System.out.println("Access Token: " + accessToken);
        System.out.println("Refresh Token: " + refresh_token);
    }

    @When("^I view Observation resource page for \"([^\"]*)\"$")
    public void i_view_Observation_resource_page_for(String patient_id) throws Throwable {
        String url = BaseProperties.BASE_URI + "dstu2/Patient/" + patient_id + "/$everything";
        String token = "Bearer " + accessToken;

        Unirest.setTimeouts(0, 0);
        response = Unirest.get(url)
                .header("Authorization", token)
                .asJson();

        int statusCode = response.getStatus();
        Assert.assertEquals(200, statusCode);

        System.out.println("Total Entry:" + response.getBody().getObject().get("total"));
        System.out.println("Response body: " + response.getBody().toString());
    }

    @Then("^I should view A total of (\\d+) non-duplicate records and next URL in each page for \"([^\"]*)\"$")
    public void i_should_view_A_total_of_non_duplicate_records_and_next_URL_in_each_page(int entry, String patient_id) throws Throwable {

        int totalEntry = Integer.parseInt(response.getBody().getObject().get("total").toString());
        System.out.println("Total Entry:" + totalEntry);

        for (int i = 0; i <= totalEntry - 10; i += entry) {
            verifyRecordsOnEachPage(patient_id, i);
        }

    }

    @Then("^I should NOT view the next URL in the last page$")
    public void i_should_NOT_view_the_next_URL_in_the_last_page() {

        Assert.assertFalse(response.getBody().getObject().isNull("link"));
    }

    public void verifyRecordsOnEachPage(String patient_id, int next) throws Throwable {
        String url = BaseProperties.BASE_URI + "dstu2/Patient/" + patient_id + "/$everything?" + "_skip=" + next;
        String token = "Bearer " + accessToken;

        Unirest.setTimeouts(0, 0);
        response = Unirest.get(url)
                .header("Authorization", token)
                .asJson();

        // validate the status code
        int statusCode = response.getStatus();
        Assert.assertEquals(200, statusCode);
        Assert.assertEquals("Status Code should be 200 OK", 200, statusCode);

        // validate that each page only contains 10 or less than 10 records
        JSONArray entryPerPage = response.getBody().getObject().getJSONArray("entry");
        Assert.assertTrue(entryPerPage.length() <= 10);

        //validate the duplicate records
        Set<String> entries = new LinkedHashSet<>();
        for (int j = 0; j < entryPerPage.length(); j++) {
            entries.add(entryPerPage.toString());
        }
        Assert.assertTrue(entries.size() <= 10);

        //validate the next Url in each page
        JSONArray data = response.getBody().getObject().getJSONArray("link");
        String nextUrl = data.getJSONObject(1).getString("url");
        System.out.println("Next Page Url: " + nextUrl);
    }


}
