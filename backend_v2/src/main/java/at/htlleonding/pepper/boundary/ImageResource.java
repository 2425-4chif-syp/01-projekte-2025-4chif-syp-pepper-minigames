package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.entity.Image;
import at.htlleonding.pepper.entity.Person;
import at.htlleonding.pepper.repository.ImageRepository;
import at.htlleonding.pepper.repository.PersonRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("image")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ImageResource {
    @Inject
    ImageRepository imageRepository;

    @GET
    public Response getAllImages(){
        List<Image> images = imageRepository.listAll();
        if (images.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(images).build();
    }

    @GET
    @Path("/{id}")
    public Response getImageById(@PathParam("id") Long id){
        Optional<Image> image = imageRepository.findByIdOptional(id);
        if (image.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(image).build();
    }

    @GET
    @Path("/person/{id}")
    public Response getImageByPersonId(@PathParam("id") Long id){
        Image image = imageRepository.findImageByPersonId(id);
        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(image).build();
    }

    @Transactional
    @POST
    public Response createImage(Image image){
        if(image == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        imageRepository.persist(image);
        return Response.ok(image).build();
    }

    @Transactional
    @DELETE
    @Path("{id}")
    public Response deleteImage(@PathParam("id") Long id){
        Image image = imageRepository.findById(id);
        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        imageRepository.delete(image);
        return Response.ok(image).build();
    }
}
