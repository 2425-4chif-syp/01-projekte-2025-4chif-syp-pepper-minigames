package htl.leonding.Controller;

import htl.leonding.DTOs.StepDTO;
import htl.leonding.DTOs.TagAlongStoryDTO;
import htl.leonding.Model.Step;
import htl.leonding.Model.TagAlongStory;
import htl.leonding.Repository.StepRepository;
import htl.leonding.Repository.TagAlongStoryRepository;
import htl.leonding.Service.Converter;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Path("/api/tagalongstories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagAlongStoriesEndpoint {

    @Inject
    TagAlongStoryRepository tagAlongStoryRepository;
    @Inject
    StepRepository stepRepository;

    @GET
    @Operation(summary = "Get all tag along stories")
    public List<TagAlongStory> GetTagAlongStories(@QueryParam("withoutDisabled") Boolean withoutDisabled)
    {
        if (withoutDisabled != null && withoutDisabled) {
            return tagAlongStoryRepository.list("isEnabled = 1");
        }
        return tagAlongStoryRepository.listAll();
    }

    @GET
    @Operation(summary = "Get one tag along story with id")
    @Path("/{id}")
    public Response GetTagAlongStoriesById(@PathParam("id") Long id)
    {
        TagAlongStory tagAlongStory = tagAlongStoryRepository.findById(id);
        if (tagAlongStory == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No tag along story found with id " + id).build();
        }
        return Response.ok(tagAlongStory).build();
    }

    @GET
    @Path("/{id}/picture")
    @Produces("image/png")
    @Operation(summary = "Get one picture per tag along story with id")
    public Response GetTagAlongStoriesPicById(@PathParam("id") Long id)
    {
        TagAlongStory tagAlongStory = tagAlongStoryRepository.findById(id);
        if (tagAlongStory == null || tagAlongStory.getStoryIcon() == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No image found for tag along story with id " + id).build();
        }
        return Response.ok(tagAlongStory.getStoryIcon()).build();
    }

    @POST
    @Transactional
    @Operation(summary = "Create one tag along story")
    public Response CreateTagAlongStories(TagAlongStoryDTO tagAlongStoryDTO)
    {
        if (tagAlongStoryDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Tag along story is null").build();
        }
        tagAlongStoryRepository.persist(Converter.ConvertToTagAlongStory(tagAlongStoryDTO));
        return Response.ok(tagAlongStoryDTO).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update one tag along story with id")
    public Response UpdateTagAlongStoriesById(@PathParam("id") Long id, TagAlongStoryDTO updatedStory)
    {
        int isEnabled = 0;
        TagAlongStory tagAlongStory = tagAlongStoryRepository.findById(id);
        if (tagAlongStory == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No tag along story found with id " + id).build();
        }
        tagAlongStory.setName(updatedStory.getName());
        tagAlongStory.setStoryIcon(Base64.getDecoder().decode(updatedStory.getStoryIcon()));
        if(Objects.equals(updatedStory.isEnabled(), "true"))
            isEnabled = 1;
        tagAlongStory.setEnabled(isEnabled);
        System.out.println(tagAlongStory);
        return Response.ok("Updated tag along story").build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete one tag along story with id")
    public Response DeleteTagAlongStoriesById(@PathParam("id") Long id)
    {
        boolean deleted = tagAlongStoryRepository.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).entity("No tag along story found with id " + id).build();
        }
        return Response.ok("Deleted tag along story").build();
    }

    @POST
    @Path("/{id}/steps")
    @Transactional
    @Operation(summary = "Create steps")
    public Response CreateStepsById(@PathParam("id") Long id, StepDTO stepDTO)
    {
        TagAlongStory tagAlongStory = tagAlongStoryRepository.findById(id);
        if (tagAlongStory == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No tag along story found with id " + id)
                    .build();
        }
        Step step = Converter.ConvertToStep(stepDTO);
        step.setTagAlongStory(tagAlongStory);
        stepRepository.persist(step);
        System.out.println(stepDTO.getIndex());
        return Response.ok(stepDTO).build();
    }


    @GET
    @Path("/{id}/steps")
    @Operation(summary = "Get steps by id")
    public Response GetStepsById(@PathParam("id") Long id)
    {
        TagAlongStory tagAlongStory = tagAlongStoryRepository.findById(id);
        if (tagAlongStory == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No tag along story found with id " + id).build();
        }
        return Response.ok(tagAlongStory.getSteps()).build(
        );
    }
}
