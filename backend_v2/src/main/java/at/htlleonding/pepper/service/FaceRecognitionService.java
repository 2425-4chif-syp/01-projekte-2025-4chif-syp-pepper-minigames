package at.htlleonding.pepper.service;

import at.htlleonding.pepper.common.Constants;
import at.htlleonding.pepper.common.AwsClientProvider;
import jakarta.ws.rs.core.Response;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FaceRecognitionService {
    public Response verifyFace(byte[] fileContent) {
        try {
            RekognitionClient rekognitionClient = AwsClientProvider.getRekognitionClient();

            SearchFacesByImageRequest searchFacesRequest = SearchFacesByImageRequest.builder()
                    .collectionId(Constants.COLLECTION_ID)
                    .image(Image.builder().bytes(SdkBytes.fromByteArray(fileContent)).build())
                    .build();
            SearchFacesByImageResponse searchFacesResponse = rekognitionClient.searchFacesByImage(searchFacesRequest);

            if (searchFacesResponse.faceMatches().isEmpty()) {
                return Response.ok("No matching person found").build();
            }

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

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error processing image").build();
        }
    }
}