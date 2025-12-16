package com.pep.mealplan.resource;

import com.pep.mealplan.entity.MealPlan;
import com.pep.mealplan.service.MealPlanService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

@Path("/mealplans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MealPlanResource {

    @Inject
    MealPlanService mealPlanService;


    // ------------------------------------------------------------
    // GET ALL
    // ------------------------------------------------------------
    @GET
    public List<MealPlan> getAll() {
        return mealPlanService.getAll();
    }


    // ------------------------------------------------------------
    // GET BY DATE: /mealplans/2025-01-15
    // ------------------------------------------------------------
    @GET
    @Path("/{date}")
    public MealPlan getByDate(@PathParam("date") String dateString) {
        LocalDate date = LocalDate.parse(dateString);
        return mealPlanService.getByDate(date);
    }


    // ------------------------------------------------------------
    // CREATE new MealPlan for one day
    // ------------------------------------------------------------
    @POST
    public Response create(MealPlan mealPlan) {
        MealPlan created = mealPlanService.create(mealPlan.date);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }


    // ------------------------------------------------------------
    // ADD FOOD (starter/main/dessert)
    // ------------------------------------------------------------
    @POST
    @Path("/{mealPlanId}/addFood/{foodId}/{type}")
    public void addFood(
            @PathParam("mealPlanId") Long mealPlanId,
            @PathParam("foodId") Long foodId,
            @PathParam("type") String type
    ) {
        mealPlanService.addFood(mealPlanId, foodId, type);
    }


    // ------------------------------------------------------------
    // REMOVE FOOD
    // ------------------------------------------------------------
    @DELETE
    @Path("/{mealPlanId}/removeFood/{foodId}/{type}")
    public void removeFood(
            @PathParam("mealPlanId") Long mealPlanId,
            @PathParam("foodId") Long foodId,
            @PathParam("type") String type
    ) {
        mealPlanService.removeFood(mealPlanId, foodId, type);
    }
}
