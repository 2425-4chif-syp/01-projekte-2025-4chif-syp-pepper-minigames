package com.pep.mealplan.resource;

import com.pep.mealplan.entity.MealPlan;
import com.pep.mealplan.service.MealPlanService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/menu")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MealPlanResource {

    @Inject
    MealPlanService service;

    // -------------------------------------------------
    // READ
    // -------------------------------------------------

    @GET
    @Path("/week/{weekNumber}")
    public List<MealPlan> getByWeek(@PathParam("weekNumber") int weekNumber) {
        return service.getByWeek(weekNumber);
    }

    @GET
    @Path("/day/{weekNumber}/{weekDay}")
    public MealPlan getByWeekAndDay(
            @PathParam("weekNumber") int weekNumber,
            @PathParam("weekDay") int weekDay
    ) {
        return service.getByWeekAndDay(weekNumber, weekDay);
    }

    // -------------------------------------------------
    // WRITE (UPSERT)
    // -------------------------------------------------

    @POST
    public Response upsertDay(MealPlan plan) {
        MealPlan saved = service.upsertDay(plan);
        return Response.ok(saved).build();
    }

    @POST
    @Path("/week")
    public Response upsertWeek(List<MealPlan> plans) {
        service.upsertWeek(plans);
        return Response.ok().build();
    }

    @DELETE
    @Path("/wipe")
    public Response wipeAll() {
        service.deleteAll();
        return Response.noContent().build();
    }
}
