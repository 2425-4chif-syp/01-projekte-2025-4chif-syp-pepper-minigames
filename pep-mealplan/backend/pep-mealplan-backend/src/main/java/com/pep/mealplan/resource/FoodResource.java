package com.pep.mealplan.resource;

import com.pep.mealplan.entity.Food;
import com.pep.mealplan.entity.FoodAllergen;
import com.pep.mealplan.resource.dto.FoodCreateRequest;
import com.pep.mealplan.service.FoodAllergenService;
import com.pep.mealplan.service.FoodService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/foods")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FoodResource {

    @Inject
    FoodService foodService;

    @GET
    public List<Food> getAll() {
        return foodService.getAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Food f = foodService.getById(id);
        if (f == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(f).build();
    }

    @GET
    @Path("/type/{type}")
    public List<Food> getByType(@PathParam("type") String type) {
        return foodService.getByType(type);
    }

    @GET
    @Path("/name/{name}")
    public Response searchByName(
            @PathParam("name") String name,
            @QueryParam("strict") @DefaultValue("false") boolean strict
    ) {
        List<Food> res = foodService.searchByName(name, strict);
        if (res.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(res).build();
    }

    @POST
    public Response create(Food food) {
        Food created = foodService.create(food);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PATCH
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Food food) {
        Food updated = foodService.update(id, food);
        if (updated == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = foodService.delete(id);
        if (!deleted) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
    }

    // ... imports

    @Inject
    FoodAllergenService foodAllergenService;

    @GET
    @Path("/{id}/allergens")
    public List<FoodAllergen> getFoodAllergens(@PathParam("id") Long id) {
        return foodAllergenService.getForFood(id);
    }

    @POST
    @Path("/{id}/allergens/{shortname}")
    public void addAllergen(@PathParam("id") Long id, @PathParam("shortname") String shortname) {
        foodAllergenService.addAllergen(id, shortname);
    }

    @DELETE
    @Path("/{id}/allergens/{shortname}")
    public void removeAllergen(@PathParam("id") Long id, @PathParam("shortname") String shortname) {
        foodAllergenService.removeAllergen(id, shortname);
    }

    @POST
    @Path("/create")
    public Response createFromDto(FoodCreateRequest dto) {
        Food created = foodService.createFromDto(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
