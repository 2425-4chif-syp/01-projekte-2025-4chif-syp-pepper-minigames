package com.pep.mealplan.resource;

import com.pep.mealplan.entity.Allergen;
import com.pep.mealplan.service.AllergenService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/allergens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AllergenResource {

    @Inject
    AllergenService service;

    // -------------------------------------------------
    // READ
    // -------------------------------------------------

    @GET
    public List<Allergen> getAll() {
        return service.findAll();
    }

    @GET
    @Path("/{shortname}")
    public Response getOne(@PathParam("shortname") String shortname) {
        Allergen allergen = service.findByShortname(shortname);
        if (allergen == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(allergen).build();
    }

    // -------------------------------------------------
    // WRITE
    // -------------------------------------------------

    @POST
    public Response create(Allergen allergen) {
        Allergen created = service.create(allergen);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    @PUT
    @Path("/{shortname}")
    public Response update(@PathParam("shortname") String shortname, Allergen allergen) {
        Allergen updated = service.update(shortname, allergen);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{shortname}")
    public Response delete(@PathParam("shortname") String shortname) {
        boolean deleted = service.delete(shortname);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
