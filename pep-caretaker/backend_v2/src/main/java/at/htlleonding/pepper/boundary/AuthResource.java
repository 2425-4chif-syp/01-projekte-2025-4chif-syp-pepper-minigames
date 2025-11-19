package at.htlleonding.pepper.boundary;

import at.htlleonding.pepper.service.FaceRecognitionService;
import at.htlleonding.pepper.service.TextVerificationService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthResource {
    private final FaceRecognitionService faceRecognitionService = new FaceRecognitionService();
    private final TextVerificationService textVerificationService = new TextVerificationService();

    @POST
    @Path("/face/verify")
    @Consumes("application/octet-stream")
    public Response recognizeFace(byte[] fileContent) {
        return faceRecognitionService.verifyFace(fileContent);
    }

    @POST
    @Path("/text/verify")
    @Consumes("text/plain")
    public Response verifyText(String text) {
        return textVerificationService.verifyText(text);
    }
}
