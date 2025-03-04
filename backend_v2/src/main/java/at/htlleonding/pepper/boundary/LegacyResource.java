package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.boundary.dto.GameDto;
import at.htlleonding.pepper.boundary.dto.StepDto;
import at.htlleonding.pepper.entity.Game;
import at.htlleonding.pepper.entity.Step;
import at.htlleonding.pepper.repository.GameRepository;
import at.htlleonding.pepper.repository.GameTypeRepository;
import at.htlleonding.pepper.repository.MoveRepository;
import at.htlleonding.pepper.repository.StepRepository;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Path("legacy/stories")
@Produces(MediaType.APPLICATION_JSON)
public class LegacyResource {

    @Inject
    GameRepository gameRepository;

    @Inject
    StepRepository stepRepository;

    @Inject
    MoveRepository moveRepository;

    @Inject
    GameTypeRepository gameTypeRepository;

    @Transactional
    @GET
    public Response findAll() {
        List<Game> stories = gameRepository.list("isEnabled = true and gameType.id = ?1", "TAG_ALONG_STORY");
        List<GameDto> storiesDtoList = new LinkedList<GameDto>();

        stories.forEach(game -> {
            List<Step> steps = stepRepository.list("game.id = ?1", game.getId());

            // find the steps for this game
            if (steps != null) {
                List<StepDto> stepDtoList = new LinkedList<>();
                steps.forEach(step -> {
                    var stepDto = new StepDto(
                            step.getId(),
                            step.getText(),
                            step.getImageBase64(),
                            step.getDurationInSeconds(),
                            step.getMove().getName()
                    );
                    stepDtoList.add(stepDto);
                });

                // create the story
                var story = new GameDto(
                        game.getId(),
                        game.getName(),
                        game.getStoryIconBase64(),
                        stepDtoList,
                        game.isEnabled()
                );
                storiesDtoList.add(story);
            } else {
                // create the story
                var story = new GameDto(
                        game.getId(),
                        game.getName(),
                        game.getStoryIconBase64(),
                        null,
                        game.isEnabled()
                );
                storiesDtoList.add(story);
            }


        });

        return Response.ok(storiesDtoList).build();
    }

    @Transactional
    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") Long id) {

        var game = gameRepository.findByIdOptional(id);

        if (game.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var steps = stepRepository.list("game.id = ?1", id);

        var stepDtoList = new LinkedList<StepDto>();

        steps.forEach(step -> {
            var stepDto = new StepDto(
                    step.getId(),
                    step.getText(),
                    step.getImageBase64(),
                    step.getDurationInSeconds(),
                    step.getMove().getName() + "_" + step.getDurationInSeconds()
            );
            stepDtoList.add(stepDto);
        });

        var gameDto = new GameDto(
                game.get().getId(),
                game.get().getName(),
                game.get().getStoryIconBase64(),
                stepDtoList,
                game.get().isEnabled()
        );

        return Response.ok(gameDto).build();

    }


    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createStory(GameDto gameDto, @Context UriInfo uriInfo) {

        Log.info(gameDto.id() + ": " + gameDto.name());

        boolean stepsExists = false;
        AtomicInteger stepIndex = new AtomicInteger(1);

        Game game = new Game();
        game.setName(gameDto.name());
        game.setEnabled(gameDto.isEnabled());
        game.setGameType(gameTypeRepository.find("id", "TAG_ALONG_STORY").firstResult());
        game.setStoryIconBase64(gameDto.storyIcon());

        gameRepository.persist(game);

        if (gameDto.steps().size() == 1 && !gameDto.steps().getFirst().text().isEmpty()) {
            stepsExists = true;
        }

        if (gameDto.steps().size() > 1) {
            stepsExists = true;
        }

        if (stepsExists) {
            Log.info("Steps exists and will be persisted");
            gameDto.steps().forEach(stepDto -> {
                Step step = new Step();
                step.setText(stepDto.text());
                Log.info("Step : " + step.getText());
                step.setDurationInSeconds(stepDto.duration());
                step.setMove(moveRepository.find("name", stepDto.moveNameAndDuration()).firstResult());
                step.setGame(game);
                step.setImageBase64(stepDto.image());
                step.setIndex(stepIndex.getAndIncrement());
                stepRepository.persist(step);
            });
        }

        UriBuilder uriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .path(game.getId().toString());

        Log.info(uriInfo
                .getAbsolutePathBuilder()
                .path(game.getId().toString()).build());

        return Response.created(uriBuilder.build()).build();

    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response addStepToStory(@PathParam("id") Long gameId, GameDto gameDto, @Context UriInfo uriInfo) {
        Log.info("PUT: id = " + gameId);
        Log.info("PUT: gameDto = " + gameDto.id() + ": " + gameDto.name());

        boolean storyFieldsHasChanged = false;
        boolean stepsExists = false;

        // Story (Game) anhand id aus DB holen
        var game = gameRepository.findById(gameId);

        // Haben sich die Felder der Story geändert?
        if (!game.getName().equals(gameDto.name())) {
            game.setName(gameDto.name());
            storyFieldsHasChanged = true;
        }

        if (!game.getStoryIconBase64().equals(gameDto.storyIcon())) {
            game.setStoryIconBase64(gameDto.storyIcon());
            storyFieldsHasChanged = true;
        }


        if (gameDto.steps().size() == 1 && !gameDto.steps().getFirst().text().isEmpty()) {
            stepsExists = true;
        }

        if (gameDto.steps().size() > 1) {
            stepsExists = true;
        }

        // TODO: Derzeit werden nur neue Steps hinzugefügt, jedoch Änderungen an bestehenden
        //  Steps nicht berücksichtigt. Das ist wahrscheinlich nicht ganz optimal.
        // Alle Steps mit id==0 speichern
        if (stepsExists) {

            // ermittle den höchsten StepIndex
            int maxIndex = stepRepository.list("game.id = ?1", gameId).size();
            AtomicInteger stepIndex = new AtomicInteger(maxIndex + 1);

            Log.info("Steps exists and will be persisted");
            gameDto.steps().stream().filter(s -> s.id() == 0).forEach(stepDto -> {
                Step step = new Step();
                step.setText(stepDto.text());
                Log.info("Step : " + step.getText());
                step.setDurationInSeconds(stepDto.duration());
                step.setMove(moveRepository.find("name", stepDto.moveNameAndDuration()).firstResult());
                step.setGame(game);
                step.setImageBase64(stepDto.image());
                step.setIndex(stepIndex.getAndIncrement());
                stepRepository.persist(step);
            });
        }

        UriBuilder uriBuilder = uriInfo
                .getAbsolutePathBuilder()
                .path(game.getId().toString());

        Log.info(uriInfo
                .getAbsolutePathBuilder()
                .path(game.getId().toString()).build());

        return Response.created(uriBuilder.build()).build();

    }

    @DELETE
    @Transactional
    @Path("{gameId}")
    public Response deleteStory(@PathParam("gameId") Long gameId) {
        boolean stepsExists = false;

        // Story (Game) anhand id aus DB holen
        var game = gameRepository.findById(gameId);

        // für die Story werden nun sämtliche Steps aus DB geholt
        var steps = stepRepository.list("game.id = ?1", gameId);


        if (steps.size() > 1) {
            stepsExists = true;
        }

        // Alle Steps löschen
        if (stepsExists) {
            Log.info("Steps exists and will be deleted");
            steps.forEach(step -> {
                Log.info("Step : '" + step.getText() + "' will be deleted in DB");
                stepRepository.delete(step);
            });
        }

        // Schließlich wird auch die Story (das Game) gelöscht
        Log.info("Story '" + game.getName() + "' will be deleted from DB");
        gameRepository.delete(game);

        return Response.noContent().build();
    }

    @Transactional
    @GET
    @Path("{id}/steps")
    public Response findAllStepsByStoryId(@PathParam("id") Long id) {
        var game = gameRepository.findByIdOptional(id);

        if (game.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var steps = stepRepository.list("game.id = ?1", id);
        return Response.ok(steps).build();
    }

    @Transactional
    @GET
    @Path("steps/{id}")
    public Response findStepById(@PathParam("id") Long id) {
        var step = stepRepository.findByIdOptional(id);
        if(step.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(step).build();
    }




}
