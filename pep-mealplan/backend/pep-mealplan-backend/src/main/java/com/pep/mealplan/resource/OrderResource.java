package com.pep.mealplan.resource;

import com.pep.mealplan.resource.dto.OrderBulkRequest;
import com.pep.mealplan.service.OrderService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderService orderService;

    // ---------------------------------------------------------
    // BULK ORDER ENDPOINT
    // ---------------------------------------------------------
    @POST
    @Path("/bulk")
    public Response createBulkOrders(OrderBulkRequest req) {

        if (req == null || req.weekStartDate == null || req.orders == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid payload: weekStartDate and orders are required")
                    .build();
        }

        orderService.bulkCreateOrders(req);
        return Response.ok().entity("Orders successfully created").build();
    }
}
