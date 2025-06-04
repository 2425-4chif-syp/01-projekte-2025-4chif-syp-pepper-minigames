package at.htlleonding.pepper.domain;

import at.htlleonding.pepper.boundary.ImageResource;
import at.htlleonding.pepper.boundary.dto.ImageDto;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;


@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageRessourceTest {

    private static final Logger log = LoggerFactory.getLogger(ImageRessourceTest.class);
    static Person person;
    static Image image;
    static long imageId;

    @Inject
    ImageResource imageResource;

    @Test
    @Order(110)
    void createPersonForImage() {
        // Arrange
        var newPerson = Map.of(
                "firstName", "Milad",
                "lastName", "Moradi",
                "birthDate", "1995-10-10",
                "roomNo", "C3",
                "isWorker", false
        );

        // Act
        person = given()
                .contentType(APPLICATION_JSON)
                .body(newPerson)
                .when().post("/api/person")
                .then().statusCode(201)
                .extract().as(Person.class);



        // Assert
        assertThat(person).isNotNull();
        assertThat(person.getFirstName()).isEqualTo("Milad");
        assertThat(person.getLastName()).isEqualTo("Moradi");
        assertThat(person.getRoomNo()).isEqualTo("C3");
    }

    @Test
    @Order(130)
    void createImage_shouldPersistAndReturn201(){
        // Arrange
        var base64Image = Base64.getEncoder().encodeToString("Hallo".getBytes());
        ImageDto imageDto = new ImageDto(null, person, base64Image, null, "Test image");

        image = given()
                .contentType(APPLICATION_JSON)
                .body(imageDto)
                .when().post("/api/image")
                .then().statusCode(201)
                .extract().as(Image.class);

        assertThat(image).isNotNull();
        assertThat(image.getDescription()).isEqualTo("Test image");
        // Act + Assert
        // Act: Erstelle das Bild
         image = given()
                .contentType(APPLICATION_JSON)
                .body(imageDto)
                .when().post("/api/image")
                .then().statusCode(201)
                .extract().as(Image.class);
    }

    @Test
    @Order(140)
    void getAllImages_shouldReturnCreatedImage() {


        // Act
        var response = given()
                .contentType(APPLICATION_JSON)
                .when().get("/api/image")
                .then().extract();

        // Assert
        if (response.statusCode() == 200) {
            var images = response.as(new TypeRef<List<Map<String, Object>>>() {});
            assertThat(images)
                    .isNotEmpty()
                    .anySatisfy(img -> {
                        assertThat(img.get("description")).isEqualTo("Test image");
                        imageId = ((Number) img.get("id")).longValue();
                    });
        } else {
            assertThat(response.statusCode()).isEqualTo(404);
        }
    }

    @Test
    @Order(145)
    void getImage_shouldReturnSameBase64() {
        // Arrange
        var expectedBase64 = Base64.getEncoder().encodeToString("Hallo".getBytes());

        // Act
        var image = given()
                .contentType(APPLICATION_JSON)
                .when().get("/api/image/" + imageId)
                .then().statusCode(200)
                .extract().as(new TypeRef<Map<String, Object>>() {});

        // Assert
        assertThat(image.get("base64Image")).isEqualTo(expectedBase64);
    }

    @Test
    @Order(150)
    void deleteImage_shouldRemoveImage() {
        // Arrange
        // (imageId existiert bereits)

        // Act
        given()
                .when().delete("/api/image/" + imageId)
                .then().statusCode(204);

        // Assert
        var images = given()
                .contentType(APPLICATION_JSON)
                .when().get("/api/image")
                .then()
                .extract().as(new TypeRef<List<Map<String, Object>>>() {});

        assertThat(images)
                .noneMatch(img -> ((Number) img.get("id")).longValue() == imageId);
    }

    @Test
    @Order(160)
    void deleteImage_shouldReturn404_ifNotFound() {
        // Arrange
        long nonExistingId = 999999;

        // Act + Assert
        given()
                .when().delete("/api/image/" + nonExistingId)
                .then().statusCode(404);
    }

    @Test
    @Order(170)
    void createImage_shouldReturn400_ifMissingBase64() {
        // Arrange
        var invalidDto = Map.of(
                "description", "Missing base64",
                "imageUrl", "http://example.com/missing.jpg",
                "person", Map.of("id", person.getId())
        );

        // Act + Assert
        given()
                .contentType(APPLICATION_JSON)
                .body(invalidDto)
                .when().post("/api/image")
                .then().statusCode(400);
    }
}
