package com.pep.mealplan.resource;

import com.pep.mealplan.resource.dto.PersonCreateRequest;
import com.pep.mealplan.resource.dto.PersonResponse;
import com.pep.mealplan.service.PersonService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {

    @Inject
    PersonService personService;

    // ---------------------------------------------------------
    // GET ALL
    // ---------------------------------------------------------
    @GET
    public List<PersonResponse> getAll() {
        return personService.getAll();
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        PersonResponse p = personService.getById(id);
        if (p == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(p).build();
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @POST
    public Response create(PersonCreateRequest req) {
        PersonResponse created = personService.create(req);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, PersonCreateRequest req) {
        PersonResponse updated = personService.update(id, req);
        if (updated == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(updated).build();
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean removed = personService.delete(id);

        if (!removed)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.noContent().build();
    }
}
