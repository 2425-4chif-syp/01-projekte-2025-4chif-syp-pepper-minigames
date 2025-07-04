    package at.htlleonding.pepper.boundary;

    import at.htlleonding.pepper.domain.Person;
    import at.htlleonding.pepper.dto.PersonDto;
    import at.htlleonding.pepper.repository.PersonRepository;
    import jakarta.enterprise.context.ApplicationScoped;
    import jakarta.inject.Inject;
    import jakarta.transaction.Transactional;
    import jakarta.ws.rs.*;
    import jakarta.ws.rs.core.MediaType;
    import jakarta.ws.rs.core.Response;
    import org.eclipse.microprofile.openapi.annotations.Operation;
    import org.mindrot.jbcrypt.BCrypt;

    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;


    @Path("person")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApplicationScoped
    public class PersonResource {
        @Inject
        PersonRepository personRepository;

        //region Person Endpoint
        @GET
        @Operation(summary = "Get all people")
        @Transactional
        public Response getAllPeople() {
            List<Person> persons = personRepository.listAll();

            if (persons.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("Keine Personen gefunden").build();
            }
            List<PersonDto> safePersons = persons.stream()
                    .map(person -> new PersonDto(
                            person.getId(),
                            person.getFirstName(),
                            person.getLastName(),
                            person.getDob(),
                            person.getRoomNo(),
                            person.getIsWorker(),
                            person.getGender()
                    ))
                    .collect(Collectors.toList());

            return Response.ok(safePersons).build();
        }

        @POST
        @Transactional
        public Response add(Person person) {

            if (person.getFirstName() == null || person.getLastName() == null || person.getRoomNo() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Fehlende Daten: Vorname, Nachname oder Zimmernummer").build();
            }
            personRepository.persist(person);
            return Response.status(Response.Status.CREATED).entity(person).build();
        }

        @GET
        @Path("/{id}")
        public Response getPersonById(@PathParam("id") Long id) {
            Person person = personRepository.findById(id);
            if (person == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Person nicht gefunden").build();
            }
            PersonDto safePerson = new PersonDto(
                    person.getId(),
                    person.getFirstName(),
                    person.getLastName(),
                    person.getDob(),
                    person.getRoomNo(),
                    person.getIsWorker(),
                    person.getGender()
            );

            return Response.ok(safePerson).build();
        }

        @PUT
        @Path("/{id}")
        @Transactional
        public Response updatePerson(@PathParam("id") Long id, Person updatedPerson) {
            Person existingPerson = personRepository.findById(id);
            if (existingPerson == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Person nicht gefunden").build();
            }

            if (updatedPerson.getFirstName() != null) existingPerson.setFirstName(updatedPerson.getFirstName());
            if (updatedPerson.getLastName() != null) existingPerson.setLastName(updatedPerson.getLastName());
            if (updatedPerson.getDob() != null) existingPerson.setDob(updatedPerson.getDob());
            if (updatedPerson.getRoomNo() != null) existingPerson.setRoomNo(updatedPerson.getRoomNo());

            return Response.ok(existingPerson).build();
        }

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
        //endregion

        //region Auth
        @POST
        @Path("/login")
        public Response login(Person loginPerson) {
            Optional<Person> personOpt = personRepository.find("firstName = ?1 AND lastName = ?2",
                    loginPerson.getFirstName(), loginPerson.getLastName()).firstResultOptional();


            if (personOpt.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Benutzer nicht gefunden").build();
            }

            Person person = personOpt.get();


            if (!Boolean.TRUE.equals(person.getIsWorker())) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Senioren benötigen kein Login").build();
            }

            boolean passwordMatches = BCrypt.checkpw(loginPerson.getPassword(), person.getPassword());


            if (!passwordMatches) {

                try {
                    rehashPasswordAndStore(person);
                    passwordMatches = BCrypt.checkpw(loginPerson.getPassword(), person.getPassword());
                } catch (IllegalArgumentException e) {
                    return Response.status(Response.Status.UNAUTHORIZED).entity("Falsches Passwort").build();
                }
            }

            if (passwordMatches) {
                return Response.ok().entity("Erfolgreich eingeloggt").build();
            }

            return Response.status(Response.Status.UNAUTHORIZED).entity("Falsches Passwort").build();
        }



        public void rehashPasswordAndStore(Person person) {

            String hashedPassword = BCrypt.hashpw(person.getPassword(), BCrypt.gensalt());


            person.setPassword(hashedPassword);
            personRepository.persist(person);
        }
        //endregion

    }


