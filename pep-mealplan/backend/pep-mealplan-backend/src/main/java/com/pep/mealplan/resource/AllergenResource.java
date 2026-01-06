package com.pep.mealplan.resource;

import com.pep.mealplan.entity.Allergen;
import com.pep.mealplan.service.AllergenService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api/allergens")
@Produces(MediaType.APPLICATION_JSON)
public class AllergenResource {

    @Inject
    AllergenService service;

    @GET
    public List<Allergen> getAll() {
        return service.findAll();
    }

    @GET
    @Path("/{shortname}")
    public Allergen getOne(@PathParam("shortname") String shortname) {
        return service.findByShortname(shortname);
    }
}
