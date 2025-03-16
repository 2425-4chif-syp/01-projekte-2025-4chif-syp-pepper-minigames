package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.domain.GameScore;
import at.htlleonding.pepper.repository.GameScoreRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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

}
