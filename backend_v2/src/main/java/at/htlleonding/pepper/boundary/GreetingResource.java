package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.domain.model.ChatGPTRequest;
import at.htlleonding.pepper.domain.model.ChatGPTResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.util.List;

@Path("/chat")
public class GreetingResource {

    //  private static final String API_KEY2 = System.getenv("mp.rest.client.chatgpt-api.auth-token");
    private static final String API_KEY = System.getenv("CHATGBT_API_KEY");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String chat(String input) {
        System.out.println("API_KEY: " + API_KEY);

        try {
            // Anfrage an die ChatGPT API vorbereiten

            ChatGPTRequest request = new ChatGPTRequest(
                    "gpt-3.5-turbo",
                    List.of(new ChatGPTRequest.Message("user", input))
            );

            // HTTP-Client initialisieren
            Response response = ClientBuilder.newClient()
                    .target(API_URL)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + API_KEY)
                    .post(Entity.json(request));

            if (response.getStatus() == 200) {
                ChatGPTResponse chatResponse = response.readEntity(ChatGPTResponse.class);
                String value = chatResponse.getChoices().get(0).getMessage().getContent();
                System.out.println(value);
                return value;
            } else {
                return "Fehler: " + response.getStatusInfo().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Fehler beim Abrufen der Antwort: " + e.getMessage();
        }
    }
}