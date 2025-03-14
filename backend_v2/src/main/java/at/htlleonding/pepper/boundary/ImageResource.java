package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.boundary.dto.ImageDto;
import at.htlleonding.pepper.common.Converter;
import at.htlleonding.pepper.domain.Image;
import at.htlleonding.pepper.repository.ImageRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Path("image")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ImageResource {
    @Inject
    ImageRepository imageRepository;

    @GET
    @Transactional
    public Response getAllImages(){
        List<Image> images = imageRepository.listAll();
        List<ImageDto> imageDtos = new ArrayList<>();
        if (images.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        for(Image image : images){
            imageDtos.add(Converter.convertToImageDto(image));
        }

        return Response.ok(imageDtos).build();
    }

    @GET
    @Transactional
    @Path("{id}")
    public Response getImageById(@PathParam("id") Long id){
        ImageDto imageDto = Converter.convertToImageDto(imageRepository.findById(id));
        return Response.ok(imageDto).build();
    }

    @POST
    @Transactional
    public Response createImage(ImageDto imageDto){
        if (imageDto.base64Image() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Image image = new Image();
        image.setImage(Base64.getDecoder().decode(imageDto.base64Image()));
        image.setPerson(imageDto.person());
        image.setDescription(imageDto.description());
        image.setUrl(imageDto.imageUrl());
        imageRepository.persist(image);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    public Response deleteImage(@PathParam("id") Long id){
        Image image = imageRepository.findById(id);
        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        imageRepository.delete(image);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}

