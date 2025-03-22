package at.htlleonding.pepper.service;

<<<<<<< HEAD
import at.htlleonding.pepper.common.Constants;
import at.htlleonding.pepper.util.AwsClientProvider;
import jakarta.ws.rs.core.Response;
=======
import at.htlleonding.pepper.util.AwsClientProvider;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
>>>>>>> 35ca0212 (feat: Add tests for Person endpoints using AssertJ #PEP104)
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.FaceMatch;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.SearchFacesByImageRequest;
import software.amazon.awssdk.services.rekognition.model.SearchFacesByImageResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FaceRecognitionService {
<<<<<<< HEAD
=======

    @ConfigProperty(name = "authentication.collection.id")
    String collectionId;

    @ConfigProperty(name = "authentication.dynamodb.table")
    String dynamodbTable;

    @ConfigProperty(name = "authentication.fullName.key")
    String fullNameKey;

    @ConfigProperty(name = "authentication.rekognitionid.key")
    String rekognitionIdKey;
>>>>>>> 35ca0212 (feat: Add tests for Person endpoints using AssertJ #PEP104)

    public Response verifyFace(byte[] fileContent) {
        try {
            RekognitionClient rekognitionClient = AwsClientProvider.getRekognitionClient();
            SearchFacesByImageResponse searchFacesResponse = searchFaces(rekognitionClient, fileContent);

            if (searchFacesResponse.faceMatches().isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No matching person found")
                        .build();
            }

            return CompletableFuture.supplyAsync(() -> processFaceMatches(searchFacesResponse)).get();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error processing image").build();
        }
    }

    private SearchFacesByImageResponse searchFaces(RekognitionClient rekognitionClient, byte[] fileContent) {
        SearchFacesByImageRequest searchFacesRequest = SearchFacesByImageRequest.builder()
                .collectionId(Constants.COLLECTION_ID)
                .image(Image.builder().bytes(SdkBytes.fromByteArray(fileContent)).build())
                .build();
        return rekognitionClient.searchFacesByImage(searchFacesRequest);
    }

    private Response processFaceMatches(SearchFacesByImageResponse searchFacesResponse) {
        DynamoDbClient dynamoDbClient = AwsClientProvider.getDynamoDbClient();

        for (FaceMatch match : searchFacesResponse.faceMatches()) {
            String faceId = match.face().faceId();
            GetItemResponse getItemResponse = fetchPersonFromDynamoDb(dynamoDbClient, faceId);

            if (getItemResponse.hasItem()) {
                String fullName = getItemResponse.item().get(Constants.FULL_NAME_KEY).s();
                return Response.ok(fullName).build();
            }
        }
        return Response.ok("No matching person found").build();
    }

    private GetItemResponse fetchPersonFromDynamoDb(DynamoDbClient dynamoDbClient, String faceId) {
        Map<String, AttributeValue> key = Map.of(Constants.REKOGNITION_ID_KEY, AttributeValue.builder().s(faceId).build());
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(Constants.DYNAMODB_TABLE)
                .key(key)
                .build();
        return dynamoDbClient.getItem(getItemRequest);
    }
}