package com.pep.mealplan.resource;

import com.pep.mealplan.entity.Picture;
import com.pep.mealplan.service.PictureService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/pictures")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PictureResource {

    @Inject
    PictureService pictureService;

    // -------------------------------------------------
    // READ
    // -------------------------------------------------

    @GET
    public List<Picture> getAll() {
        return pictureService.getAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Picture picture = pictureService.getById(id);
        if (picture == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(picture).build();
    }

    @GET
    @Path("/name/{name}")
    public List<Picture> searchByName(@PathParam("name") String name) {
        return pictureService.searchByName(name);
    }

    // -------------------------------------------------
    // WRITE
    // -------------------------------------------------

    @POST
    public Response create(Picture picture) {
        Picture created = pictureService.create(picture);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Picture picture) {
        Picture updated = pictureService.update(id, picture);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = pictureService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
