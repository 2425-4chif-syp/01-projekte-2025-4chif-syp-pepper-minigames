package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.domain.Image;
import at.htlleonding.pepper.repository.ImageRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

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
}
