package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.domain.Move;
import at.htlleonding.pepper.repository.MoveRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
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

    @POST
    @Transactional
    @Operation(summary = "Create a move")
    public Response createMove(Move move) {
        System.out.println(move);
        moveRepository.persist(move);
        return Response.status(Response.Status.CREATED).entity(move).build();
    }

    @DELETE
    @Transactional
    @Path("{id}")
    @Operation(summary = "Delete a move")
    public Response deleteMove(@PathParam("id") Long id) {
        Move move = moveRepository.findById(id);
        if (move == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Move not found for id: " + id)
                    .build();
        }
        moveRepository.delete(move);
        return Response.status(Response.Status.OK)
                .entity("Move with id " + id + " successfully deleted.")
                .build();
    }

}
