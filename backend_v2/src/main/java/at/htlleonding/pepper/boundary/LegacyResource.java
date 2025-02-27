package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.boundary.dto.GameDto;
import at.htlleonding.pepper.boundary.dto.StepDto;
import at.htlleonding.pepper.entity.Game;
import at.htlleonding.pepper.entity.Step;
import at.htlleonding.pepper.repository.GameRepository;
import at.htlleonding.pepper.repository.StepRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Path("legacy/stories")
@Produces(MediaType.APPLICATION_JSON)
public class LegacyResource {

    @Inject
    GameRepository gameRepository;

    @Inject
    StepRepository stepRepository;

    @GET
    public Response findAll() {
        List<Game> stories = gameRepository.list("isEnabled = true and gameType.id = ?1", "TAG_ALONG_STORY");
        List<GameDto> storiesDtoList = new LinkedList<GameDto>();

        stories.forEach(game -> {
            List<Step> steps = stepRepository.list("game.id = ?1", game.getId());

            // find the steps for this game
            List<StepDto> stepDtoList = new LinkedList<>();
            steps.forEach(step -> {
                var stepDto = new StepDto(
                        step.getId(),
                        step.getText(),
                        step.getImage()==null?"n/a":step.getImage().getDescription(),
                        step.getDurationInSeconds(),
                        step.getMove().getName()
                );
                stepDtoList.add(stepDto);
            });

            // create the story
            var story = new GameDto(
                    game.getId(),
                    game.getName(),
                    Arrays.toString(game.getIcon()),
                    stepDtoList,
                    game.isEnabled()
            );
            storiesDtoList.add(story);
        });

        return Response.ok(storiesDtoList).build();
    }


}
