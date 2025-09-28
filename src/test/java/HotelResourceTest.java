
import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // allow @BeforeAll non-static and instance fields
public class HotelResourceTest {

    private static final String BASE_URL = "http://localhost:7070/api/v1";
    private Integer createdHotelId;
    private Integer createdRoomId;
    private Javalin app; // only non-null if we start the server here

    @BeforeAll
    void bootServerAndRestAssured() throws InterruptedException {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(); // auto-log failures
        HibernateConfig.setTest(true); // Use Testcontainers DB + create-drop

        // If port 7070 isn't listening, start Javalin (same as your Main)
        if (!isPortOpen("localhost", 7070)) {
            app = ApplicationConfig.startServer(7070);
            waitUntilHealthy(); // ping until GET /hotel is 200
        }

        Assertions.assertTrue(isPortOpen("localhost", 7070),
                "Server is not listening on http://localhost:7070");
    }

    private boolean isPortOpen(String host, int port) {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(host, port), 300);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Poll the API until routes are ready (avoids flaky sleeps)
    private void waitUntilHealthy() throws InterruptedException {
        for (int i = 0; i < 30; i++) { // ~6 seconds total
            try {
                given().accept(ContentType.JSON)
                        .when().get("/hotel")
                        .then().statusCode(200);
                return; // healthy
            } catch (AssertionError | Exception ignored) {
                Thread.sleep(200);
            }
        }
        Assertions.fail("Server never became healthy at " + BASE_URL);
    }

    @AfterAll
    void stopServerIfWeStartedIt() {
        if (app != null) ApplicationConfig.stopServer(app);
    }

    @Test @Order(1)
    @DisplayName("Server up: GET /hotel returns 200")
    void serverIsUp() {
        given().accept(ContentType.JSON)
                .when().get("/hotel")
                .then().statusCode(200);
    }

    @Test @Order(2)
    @DisplayName("Create hotel: POST /hotel -> 201 and id")
    void createHotel() {
        String body = "{" +
                "\"name\":\"Test Hotel\"," +
                "\"address\":\"Some Street 1\"" +
                "}";

        Response res = given()
                .contentType(ContentType.JSON).accept(ContentType.JSON).body(body)
                .when()
                .post("/hotel")
                .then()
                .statusCode(anyOf(is(201), is(200)))
                .body("id", notNullValue())
                .body("name", equalTo("Test Hotel"))
                .extract().response();

        createdHotelId = res.path("id");
        Assertions.assertNotNull(createdHotelId, "Expected server to return a DB id");
    }

    @Test @Order(3)
    @DisplayName("Get by id: GET /hotel/{id} -> 200 & fields")
    void getHotelById() {
        Assumptions.assumeTrue(createdHotelId != null, "No hotel id from create step");
        given().accept(ContentType.JSON)
                .when().get("/hotel/{id}", createdHotelId)
                .then().statusCode(200)
                .body("id", equalTo(createdHotelId))
                .body("name", equalTo("Test Hotel"))
                .body("address", equalTo("Some Street 1"));
    }

    @Test @Order(4)
    @DisplayName("Add room: POST /hotel/{id}/rooms -> 200/201")
    void addRoomToHotel() {
        Assumptions.assumeTrue(createdHotelId != null, "No hotel id from create step");
        String body = "{" +
                "\"number\":103," +
                "\"price\":149.0" +
                "}";
        Response res = given()
                .contentType(ContentType.JSON).accept(ContentType.JSON).body(body)
                .when()
                .post("/hotel/{id}/rooms", createdHotelId)
                .then()
                .statusCode(anyOf(is(201), is(200)))
                .extract().response();

        try { createdRoomId = res.path("id"); } catch (Exception ignored) {}
    }

    @Test @Order(5)
    @DisplayName("List rooms: GET /hotel/{id}/rooms -> 200 & non-empty")
    void listRoomsForHotel() {
        Assumptions.assumeTrue(createdHotelId != null, "No hotel id from create step");
        given().accept(ContentType.JSON)
                .when().get("/hotel/{id}/rooms", createdHotelId)
                .then().statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test @Order(6)
    @DisplayName("Update hotel: PUT /hotel/{id} -> 200 and fields updated")
    void updateHotel() {
        Assumptions.assumeTrue(createdHotelId != null, "No hotel id from create step");
        String body = "{" +
                "\"name\":\"Test Hotel Updated\"," +
                "\"address\":\"New Address 42\"" +
                "}";
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body(body)
                .when().put("/hotel/{id}", createdHotelId)
                .then().statusCode(200)
                .body("id", equalTo(createdHotelId))
                .body("name", equalTo("Test Hotel Updated"))
                .body("address", equalTo("New Address 42"));
    }

    @Test @Order(7)
    @DisplayName("Delete hotel: DELETE /hotel/{id} -> 204/200")
    void deleteHotel() {
        Assumptions.assumeTrue(createdHotelId != null, "No hotel id from create step");
        given().accept(ContentType.JSON)
                .when().delete("/hotel/{id}", createdHotelId)
                .then().statusCode(anyOf(is(204), is(200)));
    }

    @Test @Order(8)
    @DisplayName("After delete: GET /hotel/{id} -> 404")
    void getAfterDeleteShould404() {
        Assumptions.assumeTrue(createdHotelId != null, "No hotel id from create step");
        given().accept(ContentType.JSON)
                .when().get("/hotel/{id}", createdHotelId)
                .then().statusCode(404);
    }
}