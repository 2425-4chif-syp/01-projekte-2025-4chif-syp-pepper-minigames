package com.pep.mealplan.resource;

import com.pep.mealplan.resource.dto.MealPlanCreateRequest;
import com.pep.mealplan.resource.dto.MealPlanResponse;
import com.pep.mealplan.service.MealPlanService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;

@Path("/mealplans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MealPlanResource {

    @Inject
    MealPlanService service;

    // -------------------------------------------------------
    // GET ALL
    // -------------------------------------------------------
    @GET
    public Response getAll() {
        return Response.ok(service.getAll()).build();
    }

    // -------------------------------------------------------
    // GET BY ID
    // -------------------------------------------------------
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        MealPlanResponse res = service.getById(id);
        if (res == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(res).build();
    }

    // -------------------------------------------------------
    // GET BY DATE: /mealplans/date?value=2025-01-01
    // -------------------------------------------------------
    @GET
    @Path("/date")
    public Response getByDate(@QueryParam("value") String dateString) {
        if (dateString == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing date query param").build();

        LocalDate date = LocalDate.parse(dateString);
        MealPlanResponse res = service.getByDate(date);

        if (res == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(res).build();
    }

    // -------------------------------------------------------
    // CREATE (DTO)
    // -------------------------------------------------------
    @POST
    public Response create(MealPlanCreateRequest dto) {
        MealPlanResponse created = service.createFromDto(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    // -------------------------------------------------------
    // ADD FOOD TO CATEGORY
    // POST /mealplans/{id}/add?food=10&category=starter
    // -------------------------------------------------------
    @POST
    @Path("/{id}/add")
    public Response addFood(
            @PathParam("id") Long id,
            @QueryParam("food") Long foodId,
            @QueryParam("category") String category) {

        MealPlanResponse result = service.addFood(id, foodId, category);
        if (result == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(result).build();
    }

    // -------------------------------------------------------
    // REMOVE FOOD
    // DELETE /mealplans/{id}/remove?food=10&category=main
    // -------------------------------------------------------
    @DELETE
    @Path("/{id}/remove")
    public Response removeFood(
            @PathParam("id") Long id,
            @QueryParam("food") Long foodId,
            @QueryParam("category") String category) {

        MealPlanResponse result = service.removeFood(id, foodId, category);
        if (result == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(result).build();
    }
}
