package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.domain.Move;
import at.htlleonding.pepper.repository.MoveRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.List;

@Path("move")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class MoveResource {
    @Inject
    MoveRepository moveRepository;
    
    @GET
    @Operation(summary = "Get all moves")
    public Response getAllMoves() {
        List<Move> moves = moveRepository.listAll();
        if (moves.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.ok(moves).build();
    }
}
