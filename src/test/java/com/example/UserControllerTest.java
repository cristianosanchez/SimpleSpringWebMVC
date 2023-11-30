package com.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Headers;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
public class UserControllerTest {

    private Logger log = LoggerFactory.getLogger("UserController");

    // Mac OS X, osVersion=10, browserName=Chrome, browserVersion=56
    public final Header UA_MACOSX_10_CHROME = new Header("User-Agent",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");

    @Value("${server.contextPath}")
    private String context;

    @Value("${local.server.port}")
    private int port;

    @Test
    public void shouldReturnUserInfo() throws Exception {
        String uri = resource("users/1");

        Response response = get(uri);

        // Get all headers
        Headers allHeaders = response.getHeaders();

        // Get a single header value:
        String headerName = response.getHeader("headerName");

        // Get all cookies as simple name-value pairs
        Map<String, String> allCookies = response.getCookies();

        // Get a single cookie value:
        String cookieValue = response.getCookie("cookieName");

        // Get status line
        String statusLine = response.getStatusLine();

        // Get status code
        int statusCode = response.getStatusCode();
        assertThat(statusCode, is(200));

        assertUser(response, "Cristiano");
    }

    public Response get(String uri) {
        return given()
                .header(UA_MACOSX_10_CHROME)
                .when()
                .get(uri);
    }

    public Response postJSON(String uri, String jsonRequest) {
        return given()
                .contentType("application/json")
                .header(UA_MACOSX_10_CHROME)
                .body(jsonRequest)
                .when()
                .post(uri);
    }

    private void assertUser(Response response, String username) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String json = response.asString();
        User user = mapper.readValue(json, User.class);
        assertThat(user, not(nullValue()));
        assertThat(user, instanceOf(User.class));
        assertThat(user.getName(), equalToIgnoringCase(username));

        // another way
        // CollectionType collectionType2 = TypeFactory.defaultInstance().constructCollectionType(List.class, User.class);
        // List<User> users = mapper.readValue(json, collectionType2);
        // assertThat(users.isEmpty(), is(false));
        // assertThat(users.get(0), instanceOf(User.class));
    }

    private void assertUserList(Response response, String username) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference collectionType1 = new TypeReference<List<User>>() {
        };

        String json = response.asString();
        CollectionType collectionType2 = TypeFactory.defaultInstance().constructCollectionType(List.class, User.class);
        List<User> users = mapper.readValue(json, collectionType2);
        assertThat(users.isEmpty(), is(false));
        assertThat(users.get(0), instanceOf(User.class));
    }

    public String resource(String resource) {
        String res = String.format("http://localhost:%d%s/%s", this.port, this.context, resource);
        log.debug(String.format("Resource: '%s'", res));
        return res;
    }

    public Response getUsingBasicAuthentication(String res) {
        return given().accept("application/json").auth().basic("", "").when().get(resource(res));
    }

    public String loadFromResource(String file) throws IOException {
        try {
            URL resourceURL = this.getClass().getResource(file);
            Path resPath = Paths.get(resourceURL.toURI());
            return new String(Files.readAllBytes(resPath), "UTF8");
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
}