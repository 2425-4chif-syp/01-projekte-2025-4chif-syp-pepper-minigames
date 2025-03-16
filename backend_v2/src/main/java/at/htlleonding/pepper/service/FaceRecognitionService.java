package at.htlleonding.pepper.service;

<<<<<<< HEAD
import at.htlleonding.pepper.common.Constants;
import at.htlleonding.pepper.common.AwsClientProvider;
import jakarta.ws.rs.core.Response;
=======
import at.htlleonding.pepper.util.AwsClientProvider;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
>>>>>>> main
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

<<<<<<< HEAD
import java.util.HashMap;
=======
>>>>>>> main
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FaceRecognitionService {
<<<<<<< HEAD
    public Response verifyFace(byte[] fileContent) {
        try {
            RekognitionClient rekognitionClient = AwsClientProvider.getRekognitionClient();

            SearchFacesByImageRequest searchFacesRequest = SearchFacesByImageRequest.builder()
                    .collectionId(Constants.COLLECTION_ID)
                    .image(Image.builder().bytes(SdkBytes.fromByteArray(fileContent)).build())
                    .build();
            SearchFacesByImageResponse searchFacesResponse = rekognitionClient.searchFacesByImage(searchFacesRequest);
=======

    @ConfigProperty(name = "authentication.collection.id")
    String collectionId;

    @ConfigProperty(name = "authentication.dynamodb.table")
    String dynamodbTable;

    @ConfigProperty(name = "authentication.fullName.key")
    String fullNameKey;

    @ConfigProperty(name = "authentication.rekognitionid.key")
    String rekognitionIdKey;

    public Response verifyFace(byte[] fileContent) {
        try {
            RekognitionClient rekognitionClient = AwsClientProvider.getRekognitionClient();
            SearchFacesByImageResponse searchFacesResponse = searchFaces(rekognitionClient, fileContent);
>>>>>>> main

            if (searchFacesResponse.faceMatches().isEmpty()) {
                return Response.ok("No matching person found").build();
            }

<<<<<<< HEAD
            return CompletableFuture.supplyAsync(() -> {
                DynamoDbClient dynamoDbClient = AwsClientProvider.getDynamoDbClient();

                for (FaceMatch match : searchFacesResponse.faceMatches()) {
                    String faceId = match.face().faceId();

                    Map<String, AttributeValue> key = new HashMap<>();
                    key.put(Constants.REKOGNITION_ID_KEY, AttributeValue.builder().s(faceId).build());

                    GetItemRequest getItemRequest = GetItemRequest.builder()
                            .tableName(Constants.DYNAMODB_TABLE)
                            .key(key)
                            .build();

                    GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);

                    if (getItemResponse.hasItem()) {
                        String fullName = getItemResponse.item().get(Constants.FULL_NAME_KEY).s();
                        return Response.ok("Found Person: " + fullName).build();
                    }
                }
                return Response.ok("No matching person found").build();
            }).get();

=======
            return CompletableFuture.supplyAsync(() -> processFaceMatches(searchFacesResponse)).get();
>>>>>>> main
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error processing image").build();
        }
    }
<<<<<<< HEAD
=======

    private SearchFacesByImageResponse searchFaces(RekognitionClient rekognitionClient, byte[] fileContent) {
        SearchFacesByImageRequest searchFacesRequest = SearchFacesByImageRequest.builder()
                .collectionId(collectionId)
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
                String fullName = getItemResponse.item().get(fullNameKey).s();
                return Response.ok("Found Person: " + fullName).build();
            }
        }
        return Response.ok("No matching person found").build();
    }

    private GetItemResponse fetchPersonFromDynamoDb(DynamoDbClient dynamoDbClient, String faceId) {
        Map<String, AttributeValue> key = Map.of(rekognitionIdKey, AttributeValue.builder().s(faceId).build());
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(dynamodbTable)
                .key(key)
                .build();
        return dynamoDbClient.getItem(getItemRequest);
    }
>>>>>>> main
}