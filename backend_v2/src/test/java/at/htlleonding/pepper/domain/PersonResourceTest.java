package at.htlleonding.pepper.domain;

import at.htlleonding.pepper.domain.Person;
import at.htlleonding.pepper.repository.GameScoreRepository;
import at.htlleonding.pepper.repository.PersonRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonResourceTest {

    @Inject
    PersonRepository personRepository;


    @Test
    @Order(120)
    void createPerson_shouldPersistAndReturnPerson() {
        var newPerson = Map.of(
                "firstName", "Milad",
                "lastName", "Moradi",
                "birthDate", "1995-10-10",
                "roomNo", "C3",
                "isWorker", false
        );

        var response = given()
                .contentType(APPLICATION_JSON)
                .body(newPerson)
                .when().post("/api/person")
                .then().statusCode(201)
                .extract().as(Map.class);

        assertThat(response.get("firstName")).isEqualTo("Milad");
        assertThat(response.get("lastName")).isEqualTo("Moradi");
        assertThat(response.get("roomNo")).isEqualTo("C3");
        assertThat(response).containsKey("id");


    }
    @Test
    @Order(130)
    void getPerson_shouldReturnPerson() {
        var response = given()
                .contentType(APPLICATION_JSON)
                .when().get("/api/person")
                .then().statusCode(200)
                .extract().as(new TypeRef<List<Map<String, Object>>>() {});

        assertThat(response)
                .isNotEmpty()
                .anySatisfy(person -> {
                    assertThat(person.get("firstName")).isEqualTo("Milad");
                    assertThat(person.get("lastName")).isEqualTo("Moradi");
                    assertThat(person.get("roomNo")).isEqualTo("C3");
                });
    }
    @Test
    @Order(140)
    void updatePerson_shouldPersistAndReturnPerson() {

        var allPersons = given()
                .contentType(APPLICATION_JSON)
                .when().get("/api/person")
                .then().statusCode(200)
                .extract().as(new TypeRef<List<Map<String, Object>>>() {});


        var existingPerson = allPersons.stream()
                .filter(p -> "Milad".equals(p.get("firstName")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Person nicht gefunden"));

        var personId = existingPerson.get("id");


        var updatedPerson = Map.of(
                "id", personId,
                "firstName", "Milad",
                "lastName", "Updated", // z. B. Nachname geändert
                "birthDate", "1995-10-10",
                "roomNo", "D5",
                "isWorker", false
        );


        var response = given()
                .contentType(APPLICATION_JSON)
                .body(updatedPerson)
                .when().put("/api/person/" + personId)
                .then().statusCode(200)
                .extract().as(Map.class);


        assertThat(response.get("id")).isEqualTo(personId);
        assertThat(response.get("lastName")).isEqualTo("Updated");
        assertThat(response.get("roomNo")).isEqualTo("D5");
    }
    @Test
    @Order(150)
    void deletePerson_shouldPersistAndReturnPerson() {

        var allPersons = given()
                .contentType(APPLICATION_JSON)
                .when().get("/api/person")
                .then().statusCode(200)
                .extract().as(new TypeRef<List<Map<String, Object>>>() {});


        var existingPerson = allPersons.stream()
                .filter(p -> "Milad".equals(p.get("firstName")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Person nicht gefunden"));

        var personId = existingPerson.get("id");

        given()
                .when().delete("/api/person/" + personId)
                .then().statusCode(200);


        var remainingPersons = given()
                .contentType(APPLICATION_JSON)
                .when().get("/api/person")
                .then().statusCode(200)
                .extract().as(new TypeRef<List<Map<String, Object>>>() {});

        assertThat(remainingPersons)
                .noneMatch(p -> personId.equals(p.get("id")));
    }

}
