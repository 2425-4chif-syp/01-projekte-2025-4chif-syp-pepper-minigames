package com.pep.mealplan.resource;

import com.pep.mealplan.entity.Order;
import com.pep.mealplan.resource.dto.OrderCreateDTO;
import com.pep.mealplan.service.OrderService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

import com.pep.mealplan.resource.dto.KitchenSummary;


@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderService service;

    // -------------------------------------------------
    // READ
    // -------------------------------------------------

    @GET
    public List<Order> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Order order = service.getById(id);
        return order == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(order).build();
    }

    @GET
    @Path("/date/{date}")
    public List<Order> getByDate(@PathParam("date") LocalDate date) {
        return service.getByDate(date);
    }

    // -------------------------------------------------
    // WRITE
    // -------------------------------------------------

    /**
     * Upsert:
     * eine Bestellung pro Person und Tag
     */
    @PUT
    @Path("/by-user-date")
    public Order upsert(Order order) {
        return service.upsert(order);
    }

    @POST
    public Response create(OrderCreateDTO dto) {
        return Response.status(Response.Status.CREATED)
                .entity(service.create(dto))
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id)
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    // -------------------------------------------------
    // EXPORT (Küche)
    // -------------------------------------------------

    @GET
    @Path("/export/{date}")
    public List<Order> export(@PathParam("date") LocalDate date) {
        return service.exportForWeek(date);
    }

    @GET
    @Path("/kitchen/{date}")
    public KitchenSummary kitchenSummary(@PathParam("date") LocalDate date) {
        return service.kitchenSummaryForWeek(date);
    }

}
