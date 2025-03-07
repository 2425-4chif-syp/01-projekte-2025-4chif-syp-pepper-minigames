package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.entity.Person;
import at.htlleonding.pepper.repository.PersonRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;


@Path("person")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class PersonController {

    @Inject
    PersonRepository personRepository;

    // 🔍 GET: Alle Personen abrufen
    @GET
    public Response getAllPersons() {
        List<Person> persons = personRepository.listAll();

        if (persons.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("Keine Personen gefunden").build();
        }

        return Response.ok(persons).build();
    }

    // ✨ POST: Neue Person hinzufügen
    @POST
    @Transactional
    public Response addPerson(Person person) {
        if (person.getFirstName() == null || person.getLastName() == null || person.getRoomNo() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Fehlende Daten: Vorname, Nachname oder Zimmernummer").build();
        }
        personRepository.persist(person);
        return Response.status(Response.Status.CREATED).entity(person).build();
    }

    // 🔍 GET: Eine einzelne Person nach ID abrufen
    @GET
    @Path("/{id}")
    public Response getPersonById(@PathParam("id") Long id) {
        Person person = personRepository.findById(id);
        if (person == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Person nicht gefunden").build();
        }
        return Response.ok(person).build();
    }

    // ✏️ PUT: Person aktualisieren
    @PUT
    @Path("/{id}")
    @Transactional
    public Response updatePerson(@PathParam("id") Long id, Person updatedPerson) {
        Person existingPerson = personRepository.findById(id);
        if (existingPerson == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Person nicht gefunden").build();
        }

        // 🛠 Aktualisierung der Werte
        if (updatedPerson.getFirstName() != null) existingPerson.setFirstName(updatedPerson.getFirstName());
        if (updatedPerson.getLastName() != null) existingPerson.setLastName(updatedPerson.getLastName());
        if (updatedPerson.getDob() != null) existingPerson.setDob(updatedPerson.getDob());
        if (updatedPerson.getRoomNo() != null) existingPerson.setRoomNo(updatedPerson.getRoomNo());

        return Response.ok(existingPerson).build();
    }

    // 🗑 DELETE: Person löschen
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletePerson(@PathParam("id") Long id) {
        boolean deleted = personRepository.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).entity("Person nicht gefunden").build();
        }
        return Response.ok("Person gelöscht").build();
    }

    @POST
    @Path("/login")
    public Response login(Person loginPerson) {
        // Suche die Person in der Datenbank
        Optional<Person> personOpt = personRepository.find("firstName = ?1 AND lastName = ?2",
                loginPerson.getFirstName(), loginPerson.getLastName()).firstResultOptional();

        // Überprüfe, ob der Benutzer gefunden wurde
        if (personOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Benutzer nicht gefunden").build();
        }

        Person person = personOpt.get();

        // Senioren haben kein Passwort und dürfen sich nicht anmelden
        if (!Boolean.TRUE.equals(person.getIsWorker())) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Senioren benötigen kein Login").build();
        }

        // Überprüfe, ob das Passwort korrekt ist
        boolean passwordMatches = BCrypt.checkpw(loginPerson.getPassword(), person.getPassword());

        // Wenn das Passwort nicht korrekt ist, versuche, es neu zu hashen und zu speichern
        if (!passwordMatches) {
            // Überprüfe, ob der Salt möglicherweise veraltet ist
            try {
                // Hash das Passwort neu, falls das Format des gespeicherten Hashs ungültig ist
                rehashPasswordAndStore(person);
                passwordMatches = BCrypt.checkpw(loginPerson.getPassword(), person.getPassword());
            } catch (IllegalArgumentException e) {
                // Falls der Salt immer noch ungültig ist
                return Response.status(Response.Status.UNAUTHORIZED).entity("Falsches Passwort").build();
            }
        }

        // Erfolgreiches Login
        if (passwordMatches) {
            return Response.ok().entity("Erfolgreich eingeloggt").build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).entity("Falsches Passwort").build();
    }


    // Methode, um das Passwort neu zu hashen und in der Datenbank zu speichern, falls erforderlich
    public void rehashPasswordAndStore(Person person) {
        // Hash das Passwort neu mit BCrypt
        String hashedPassword = BCrypt.hashpw(person.getPassword(), BCrypt.gensalt());

        // Speichere das neu gehashte Passwort in der Datenbank
        person.setPassword(hashedPassword);
        personRepository.persist(person);
    }
    @POST
    @Transactional
    @Path("/addPerson")
    public Response Register(Person person) {
        if (person.getFirstName() == null || person.getLastName() == null || person.getRoomNo() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Fehlende Daten: Vorname, Nachname oder Zimmernummer").build();
        }

        // Hash das Passwort vor dem Speichern
        String hashedPassword = BCrypt.hashpw(person.getPassword(), BCrypt.gensalt());
        person.setPassword(hashedPassword); // Setze den gehashten Wert in die Person

        personRepository.persist(person);
        return Response.status(Response.Status.CREATED).entity(person).build();
    }

}



