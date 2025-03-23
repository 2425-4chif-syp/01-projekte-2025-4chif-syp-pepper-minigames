package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.domain.GameScore;
import at.htlleonding.pepper.repository.GameScoreRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("gamescore")
public class GameScoreResource {
    @Inject
    GameScoreRepository gameScoreRepository;

    @GET
    public Response getGameScores() {
        List<GameScore> gameScores = gameScoreRepository.listAll();
        if (gameScores.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(gameScores).build();
    }

    // GET game score by composite key (gameId, playerId)
    @GET
    @Path("{gameId}/{playerId}")
    public Response getGameScoreById(@PathParam("gameId") Long gameId, @PathParam("playerId") Long playerId) {
        GameScore gameScore = gameScoreRepository.findByGameAndPlayer(gameId, playerId);
        if (gameScore == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(gameScore).build();
    }

    // GET scores for a specific game
    @GET
    @Path("game/{gameId}")
    public Response getScoresByGame(@PathParam("gameId") Long gameId) {
        List<GameScore> scores = gameScoreRepository.findByGame(gameId);
        return Response.ok(scores).build();
    }

    // GET scores for a specific player
    @GET
    @Path("player/{playerId}")
    public Response getScoresByPlayer(@PathParam("playerId") Long playerId) {
        List<GameScore> scores = gameScoreRepository.findByPlayer(playerId);
        return Response.ok(scores).build();
    }

    // GET top scores (highest first, optional limit)
    @GET
    @Path("top")
    public Response getTopScores(@QueryParam("limit") @DefaultValue("10") int limit) {
        List<GameScore> scores = gameScoreRepository.findTopScores(limit);
        return Response.ok(scores).build();
    }

    // GET scores sorted by date (latest first)
    @GET
    @Path("latest")
    public Response getLatestScores(@QueryParam("limit") @DefaultValue("10") int limit) {
            List<GameScore> scores = gameScoreRepository.findLatestScores(limit);
            return Response.ok(scores).build();
    }

    // POST - Create a new game score
    @POST
    @Transactional
    public Response createGameScore(GameScore gameScore) {
        gameScoreRepository.persist(gameScore);
        return Response.status(Response.Status.CREATED).entity(gameScore).build();
    }

    // PUT - Update existing game score
    @PUT
    @Path("{gameId}/{playerId}")
    @Transactional
    public Response updateGameScore(@PathParam("gameId") Long gameId, @PathParam("playerId") Long playerId, GameScore updatedGameScore) {
        GameScore existingGameScore = gameScoreRepository.findByGameAndPlayer(gameId, playerId);
        if (existingGameScore == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        existingGameScore.setScore(updatedGameScore.getScore());
        existingGameScore.setDateTime(updatedGameScore.getDateTime());

        gameScoreRepository.persist(existingGameScore);
        return Response.ok(existingGameScore).build();
    }

    // DELETE - Remove a game score by ID
    @DELETE
    @Path("{gameId}/{playerId}")
    @Transactional
    public Response deleteGameScore(@PathParam("gameId") Long gameId, @PathParam("playerId") Long playerId) {
        GameScore gameScore = gameScoreRepository.findByGameAndPlayer(gameId, playerId);
        if (gameScore == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        gameScoreRepository.delete(gameScore);
        return Response.noContent().build();
    }

}
