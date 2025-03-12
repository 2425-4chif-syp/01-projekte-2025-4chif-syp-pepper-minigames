package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.domain.Image;
import at.htlleonding.pepper.dto.GameDto;
import at.htlleonding.pepper.dto.StepDto;
import at.htlleonding.pepper.domain.Game;
import at.htlleonding.pepper.domain.Step;
import at.htlleonding.pepper.repository.GameRepository;
import at.htlleonding.pepper.repository.ImageRepository;
import at.htlleonding.pepper.repository.StepRepository;
import at.htlleonding.pepper.common.Converter;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.List;

@Path("tagalongstories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TagAlongStoryResource {
    @Inject
    GameRepository gameRepository;

    @Inject
    StepRepository stepRepository;

    @Inject
    ImageRepository imageRepository;


    //region TagAlongStory Endpoints
    @GET
    @Operation(summary = "Get all tag along stories")
    @Transactional
    public Response getAllTagAlongStory(@QueryParam("withoutDisabled") Boolean withoutDisabled) {
        List<Game> tagAlongStories;
        if (withoutDisabled != null && withoutDisabled) {
            tagAlongStories = gameRepository.list("isEnabled = true and gameType.id = ?1", "TAG_ALONG_STORY");
        } else {
            tagAlongStories = gameRepository.list("gameType.id = ?1", "TAG_ALONG_STORY");
        }

        if (tagAlongStories == null || tagAlongStories.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No tag along stories found.")
                    .build();
        }
        return Response.ok(tagAlongStories).build();
    }

    @GET
    @Operation(summary = "Get one tag along story with id")
    @Path("/{id}")
    public Response getTagAlongStoriesById(@PathParam("id") Long id)
    {
        Game tagalongstory = gameRepository.find("id = ?1 and gameType.id = ?2", id, "TAG_ALONG_STORY").firstResult();
        if (tagalongstory == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(tagalongstory).build();
    }

    @GET
    @Path("/{id}/image")
    @Produces("image/png")
    @Operation(summary = "Get one image per tag along story with id")
    public Response GetTagAlongStoriesPicById(@PathParam("id") Long id) {
        Game tagAlongStory = gameRepository.find("id = ?1 and gameType.id = ?2", id, "TAG_ALONG_STORY").firstResult();
        if (tagAlongStory == null || tagAlongStory.getStoryIconBinary() == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No image found for tag along story with id " + id).build();
        }
        return Response.ok(tagAlongStory.getStoryIconBinary()).build();
    }

    @POST
    @Transactional
    @Operation(summary = "Create one tag along story")
    public Response CreateTagAlongStories(GameDto gameDTO) {
        if (gameDTO == null) {
            Log.error("Tag along story is NULL");
            return Response.status(Response.Status.BAD_REQUEST).entity("Tag along story is NULL").build();
        } else if (gameDTO.icon() == null) {
            Log.error("The Icon of Tag along story is NULL");
            return Response.status(Response.Status.BAD_REQUEST).entity("The Icon of Tag along story is NULL").build();
        }
        Game tagAlongStory = Converter.convertToTagAlongStory(gameDTO);
        // create clean base64 string without the prefix
        String base64 = Converter.extractBase64String(gameDTO.icon());
        // create image reference by saving the image to pe_image table and the foreign key to the game story icon
        Image image = new Image(null, java.util.Base64.getDecoder().decode(base64), null, "Bild für mit-mach-geschichte");
        imageRepository.persist(image);
        tagAlongStory.setStoryIcon(image);
        gameRepository.persist(tagAlongStory);
        return Response.ok(tagAlongStory).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update one tag along story with id")
    public Response UpdateTagAlongStoriesById(@PathParam("id") Long id, GameDto gameDTO){
        Game existingGame = gameRepository.findById(id);
        if (existingGame == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Game with id " + id + " not found.")
                    .build();
        }
        Game updatedGame = Converter.convertToTagAlongStory(gameDTO);
        existingGame.setName(updatedGame.getName());
        existingGame.setEnabled(updatedGame.isEnabled());
        existingGame.setStoryIconBinary(updatedGame.getStoryIconBinary());
        return Response.ok(existingGame).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete one tag along story with id")
    public Response DeleteTagAlongStoriesById(@PathParam("id") Long id){
        boolean deleted = gameRepository.deleteGameAndSteps(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).entity("No tag along story found with id " + id).build();
        }
        return Response.ok("Deleted tag along story").build();
    }
    //endregion


    //region Steps Endpoints
    @Transactional
    @GET
    @Path("/{id}/steps")
    @Operation(summary = "Get all steps by game id")
    public Response GetStepsById(@PathParam("id") Long id){
        List<Step> steps = stepRepository.findByGameId(id);
        if (steps.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No tag along story found with id " + id).build();
        }
        return Response.ok(steps).build();
    }

    @POST
    @Path("/{id}/steps")
    @Transactional
    @Operation(summary = "Create step by game id")
    public Response CreateStepsById(StepDto stepDTO, @PathParam("id") Long id){
        if (stepDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Step story is null").build();
        }

        Step step = Converter.convertToStep(stepDTO);
        step.setImageBase64(stepDTO.image());

        String base64 = Converter.extractBase64String(stepDTO.image());
        Image image = new Image(null, java.util.Base64.getDecoder().decode(base64), null, "Bild für die Szenen");
        imageRepository.persist(image);
        step.setImage(image);

        Game game = gameRepository.findById(id);
        step.setGame(game);
        imageRepository.persist(step.getImage());
        stepRepository.persist(step);
        return Response.ok(step).build();
    }

    @DELETE
    @Path("/{id}/steps/{stepId}")
    @Transactional
    @Operation(summary = "Delete step by ID")
    public Response deleteStepById(@PathParam("id") Long gameId, @PathParam("stepId") Long stepId) {
        Step step = stepRepository.findById(stepId);
        if (step == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Step not found").build();
        }
        if (!step.getGame().getId().equals(gameId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Step does not belong to the specified Game").build();
        }
        stepRepository.delete(step);
        return Response.ok(step).build();
    }
    //endregion

}
