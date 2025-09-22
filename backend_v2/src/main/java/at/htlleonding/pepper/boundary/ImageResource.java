package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.boundary.dto.ImageDto;
import at.htlleonding.pepper.common.Converter;
import at.htlleonding.pepper.domain.Image;
import at.htlleonding.pepper.domain.Person;
import at.htlleonding.pepper.repository.ImageRepository;
import io.quarkus.panache.common.Page;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.*;



import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Path("image")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ImageResource {
    @Inject
    ImageRepository imageRepository;
    @Inject
    EntityManager em;
    @Inject
    UriInfo uriInfo;

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
    @GET
    @Transactional
    @Path("/picture/{id}")
    public Response getPictureById(@PathParam("id") Long id){
        ImageDto imageDto = Converter.convertToImageDto(imageRepository.findById(id));
        var base = imageDto.base64Image();
        byte[] imageBytes = Base64.getDecoder().decode(base);
        String mime = detectMime(imageBytes);
        return Response.ok(imageBytes)
                .type(mime)
                .header("Content-Disposition", "inline; filename=\"image-" + id + extFromMime(mime) + "\"")
                .build();
    }
    @GET
    @Path("/pictures")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response listAll() {
        var images = imageRepository.listAll();
        var items = images.stream().map(img -> Map.of(
                "id", img.getId(),
                "description", img.getDescription(),
                "href", "http://localhost:8080/api/image/picture/" + img.getId(),
                "person", img.getPerson() != null ? img.getPerson().getId() : null
        )).toList();

        return Response.ok(Map.of(
                "total", items.size(),
                "items", items
        )).build();
    }

    private String detectMime(byte[] b) {
        if (b.length>=8 && (b[0]&0xFF)==0x89 && b[1]==0x50 && b[2]==0x4E && b[3]==0x47) return "image/png";
        if (b.length>=3 && (b[0]&0xFF)==0xFF && (b[1]&0xFF)==0xD8 && (b[2]&0xFF)==0xFF) return "image/jpeg";
        if (b.length>=6 && b[0]==0x47 && b[1]==0x49 && b[2]==0x46 && b[3]==0x38) return "image/gif";
        if (b.length>=12 && b[0]==0x52 && b[1]==0x49 && b[2]==0x46 && b[3]==0x46 &&
                b[8]==0x57 && b[9]==0x45 && b[10]==0x42 && b[11]==0x50) return "image/webp";
        return "application/octet-stream";
    }
    private String extFromMime(String mime) {
        return switch (mime) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }

    @POST
    @Transactional
    public Response createImage(ImageDto imageDto){
        if (imageDto.base64Image() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Person personRef = em.getReference(Person.class, imageDto.personId()); // managed Proxy
        Image image = new Image();
        image.setImage(Base64.getDecoder().decode(imageDto.base64Image()));
        image.setPerson(personRef);
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
    @GET
    @Path("/person/{personId}")
    @Transactional
    public Response getImagesByPersonId(@PathParam("personId") Long personId) {
        List<Image> images = imageRepository.find("person.id", personId).list();
        if (images.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<ImageDto> imageDtos = new ArrayList<>();
        for (Image image : images) {
            imageDtos.add(Converter.convertToImageDto(image));
        }

        return Response.ok(imageDtos).build();
    }



}

