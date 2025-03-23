package at.htlleonding.pepper.domain;

import at.htlleonding.pepper.boundary.PersonResource;
import at.htlleonding.pepper.domain.Person;
import at.htlleonding.pepper.dto.PersonDto;
import at.htlleonding.pepper.repository.PersonRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.*;

import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonResourceTest {

    private static final Logger LOG = Logger.getLogger(PersonResourceTest.class.getSimpleName());

    @Inject
    PersonResource personResource;

    @Inject
    PersonRepository personRepository;

    @Test
    @Order(130)
    @Transactional
    void getAllPeople_shouldReturnList_whenNotEmpty() {

        Person person = new Person("Amir", "Mohammadi", LocalDate.of(2000, 2, 3), "101", false, null);
        personRepository.persist(person);


        var response = personResource.getAllPeople();
        LOG.info(response.getEntity());


        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        List<?> persons = (List<?>) response.getEntity();
        assertThat(persons).isNotEmpty();
    }

    @Test
    @Order(140)
    @Transactional
    void getPersonById_shouldReturnCorrectPerson() {
        // arrange
        Person p = new Person("Tom", "Tester", LocalDate.of(1970, 5, 15), "Z1", false, null);
        personRepository.persist(p);
        Long id = p.getId();


        var response = personResource.getPersonById(id);
        LOG.info(response.getEntity());


        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(response.getEntity()).isInstanceOf(PersonDto.class);
        PersonDto dto = (PersonDto) response.getEntity();
        assertThat(dto.firstName()).isEqualTo("Tom");
        assertThat(dto.lastName()).isEqualTo("Tester");
    }

    @Test
    @Order(150)
    @Transactional
    void updatePerson_shouldModifyFieldsCorrectly() {
        // arrange
        Person p = new Person("Karl", "Alt", LocalDate.of(1965, 4, 12), "A1", true, null);
        personRepository.persist(p);

        Person updated = new Person("Karlheinz", "Alt", null, "B2", true, null);

        // act
        var response = personResource.updatePerson(p.getId(), updated);
        LOG.info(response.getEntity());

        // assert
        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        Person result = (Person) response.getEntity();
        assertThat(result.getFirstName()).isEqualTo("Karlheinz");
        assertThat(result.getRoomNo()).isEqualTo("B2");
    }

    @Test
    @Order(160)
    @Transactional
    void deletePerson_shouldRemoveCorrectly() {
        // arrange
        Person p = new Person("Delete", "Me", LocalDate.of(1950, 1, 1), "X1", false, null);
        personRepository.persist(p);
        Long id = p.getId();

        // act
        var response = personResource.deletePerson(id);
        LOG.info(response.getEntity());

        // assert
        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(personRepository.findById(id)).isNull();
    }

    @Test
    @Order(170)
    @Transactional
    void login_shouldSucceed_withCorrectCredentials() {
        // arrange
        String password = org.mindrot.jbcrypt.BCrypt.hashpw("geheim", org.mindrot.jbcrypt.BCrypt.gensalt());
        Person worker = new Person("Max", "Mustermann", LocalDate.of(1980, 3, 20), null, true, password);
        personRepository.persist(worker);

        Person loginAttempt = new Person("Max", "Mustermann", null, null, true, "geheim");

        // act
        var response = personResource.login(loginAttempt);
        LOG.info(response.getEntity());

        // assert
        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(response.getEntity()).isEqualTo("Erfolgreich eingeloggt");
    }

    @Test
    @Order(180)
    @Transactional
    void login_shouldFail_forSenior() {
        // arrange
        Person senior = new Person("Opa", "Test", LocalDate.of(1945, 6, 30), "Z3", false, null);
        personRepository.persist(senior);

        Person loginAttempt = new Person("Opa", "Test", null, "Z3", false, "egal");

        // act
        var response = personResource.login(loginAttempt);
        LOG.info(response.getEntity());

        // assert
        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED.getStatusCode());
        assertThat(response.getEntity()).isEqualTo("Senioren benötigen kein Login");
    }
    @Test
    @Order(190)
    @Transactional
    void deletePerson_shouldSucceed_withCorrectCredentials() {
        Person  person = new Person("Max", "Mustermann", null, null, true, "geheim");
        personRepository.persist(person);
        Long id = person.getId();
        var response = personResource.deletePerson(id);
        LOG.info(response.getEntity());
        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(personRepository.findById(id)).isNull();



    }
}
