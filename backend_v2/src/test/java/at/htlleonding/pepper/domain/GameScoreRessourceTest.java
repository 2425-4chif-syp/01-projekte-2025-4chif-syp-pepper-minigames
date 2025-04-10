package at.htlleonding.pepper.domain;

import at.htlleonding.pepper.repository.GameRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;

import java.util.Base64;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameScoreRessourceTest {

    static {
        RestAssured.defaultParser = Parser.JSON;
    }

    static Long createdGameId;
    static Long createdPersonId;


    @Inject
    GameRepository gameRepository;

    @Test
    @Order(110)
    void createPersonForGameScore() {
        // Arrange
        var newPerson = Map.of(
                "firstName", "Milad",
                "lastName", "Moradi",
                "birthDate", "1995-10-10",
                "roomNo", "C3",
                "isWorker", false
        );

        var created = given()
                .contentType(APPLICATION_JSON)
                .body(newPerson)
                .when().post("/api/person")
                .then().statusCode(201)
                .extract().as(Map.class);

        createdPersonId = ((Number) created.get("id")).longValue();

        assertThat(createdPersonId).isNotNull();
    }

    @Test
    @Order(120)
    void createTagAlongStoryGameOnly() {
        var base64Image = Base64.getEncoder().encodeToString("Hallo".getBytes());

        var newGame = Map.of(
                "name", "Testgeschichte",
                "icon", base64Image,
                "isEnabled", true,
                "gameType", Map.of(
                        "id", "TAG_ALONG_STORY"
                )
        );
        var response = given()
                .contentType("application/json")
                .body(newGame)
                .when().post("/api/tagalongstories")
                .then().statusCode(201)
                .extract().as(Map.class);
        createdGameId = ((Number) response.get("id")).longValue();
        assertThat(createdGameId).isNotNull();
    }
    @Test
    @Order(130)
    void CreatGameScore(){
        var person = Map.of(
                "id",createdPersonId
        );
        var game = Map.of(
                "id",createdGameId
        );
        var newGameScore = Map.of(
                "dateTime","2024-03-20T12:30:00",
                "game",game,
                "person",person,
                "score", "250"
        );
        var response = given().contentType(APPLICATION_JSON)
                .body(newGameScore).when().post("/api/gamescore")
                .then().statusCode(201);

    }
}