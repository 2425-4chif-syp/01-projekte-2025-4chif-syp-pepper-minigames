package at.htlleonding.pepper.domain;

import at.htlleonding.pepper.dto.PersonDto;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class PersonResourceTest {

    @Test
    @TestTransaction
    public void testWorkerCreation() {
        Person person = new Person(
                "Amir",
                "Mohammadi",
                LocalDate.parse("2000-04-03"),
                "101",
                false,
                null
        );

        RestAssured.given()
                .contentType("application/json")
                .body(person)
                .when().post("/api/person")
                .then()
                .log().all()
                .statusCode(201);
    }

    @Test
    @TestTransaction
    public void testGetPersons() {
        Response response = RestAssured.given()
                .when().get("/api/person")
                .then().statusCode(200)
                .extract().response();

        List<PersonDto> persons = response.jsonPath().getList(".", PersonDto.class);

        List<String> firstNames = persons.stream().map(PersonDto::getFirstName).toList();
        List<String> lastNames = persons.stream().map(PersonDto::getLastName).toList();

        assertThat(firstNames).contains("Amir");
        assertThat(lastNames).contains("Mohammadi");
    }

}
