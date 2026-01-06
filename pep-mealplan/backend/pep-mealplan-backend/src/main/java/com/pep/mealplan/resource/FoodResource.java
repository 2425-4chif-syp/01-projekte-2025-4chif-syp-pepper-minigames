package com.pep.mealplan.resource;

import com.pep.mealplan.entity.Food;
import com.pep.mealplan.service.FoodService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/foods")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FoodResource {

    @Inject
    FoodService foodService;

    // -------------------------------------------------
    // READ
    // -------------------------------------------------

    @GET
    public List<Food> getAll() {
        return foodService.getAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Food food = foodService.getById(id);
        if (food == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(food).build();
    }

    @GET
    @Path("/type/{type}")
    public List<Food> getByType(@PathParam("type") String type) {
        return foodService.getByType(type);
    }

    @GET
    @Path("/name/{name}")
    public List<Food> getByName(@PathParam("name") String name) {
        return foodService.searchByName(name);
    }

    // -------------------------------------------------
    // WRITE
    // -------------------------------------------------

    @POST
    public Response create(Food food) {
        Food created = foodService.create(food);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = foodService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
