package at.htlleonding.pepper.service;

import at.htlleonding.pepper.common.Constants;
import at.htlleonding.pepper.util.AwsClientProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TextVerificationService {


    public Response verifyText(String text) {

        try (DynamoDbClient dynamoDbClient = AwsClientProvider.getDynamoDbClient()) {
            text = text.trim().replaceAll("\\s+", " ");

            Map<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":fullName", AttributeValue.builder().s(text).build());

            String filterExpression = "FullName = :fullName";

            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(Constants.DYNAMODB_TABLE)
                    .filterExpression(filterExpression)
                    .expressionAttributeValues(expressionValues)
                    .build();

            System.out.println("Searching for FullName in DynamoDB: " + text);
            System.out.println("Collection ID: " + Constants.DYNAMODB_TABLE);
            ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);

            List<Map<String, AttributeValue>> items = scanResponse.items();
            if (items != null && !items.isEmpty()) {
                System.out.println("FullName found: " + text);
                return Response.ok("Access granted").build();
            } else {
                System.out.println("FullName not found: " + text);
                return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
            }
        } catch (DynamoDbException e) {
            System.err.println("Error accessing DynamoDB: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error processing request").build();
        }
    }
}