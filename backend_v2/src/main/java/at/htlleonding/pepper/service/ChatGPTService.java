package at.htlleonding.pepper.service;

import at.htlleonding.pepper.entity.model.ChatGPTRequest;
import at.htlleonding.pepper.entity.model.ChatGPTResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;


@Path("/v1/chat/completions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ChatGPTService {

    @POST
    ChatGPTResponse sendMessage(ChatGPTRequest request);
}
