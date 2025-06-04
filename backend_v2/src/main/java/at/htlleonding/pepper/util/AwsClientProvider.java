package at.htlleonding.pepper.util;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

public class AwsClientProvider {
    @ConfigProperty(name = "AWS_ACCESS_KEY_ID")
    String awsAccessKeyId;
    private static final StaticCredentialsProvider credentials = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                    System.getenv("AWS_ACCESS_KEY_ID"),
                    System.getenv("AWS_SECRET_ACCESS_KEY")
            ));

    public static RekognitionClient getRekognitionClient() {
        return RekognitionClient.builder()
                .credentialsProvider(credentials)
                .region(Region.US_EAST_1)
                .build();
    }

    public static DynamoDbClient getDynamoDbClient() {
        return DynamoDbClient.builder()
                .credentialsProvider(credentials)
                .region(Region.US_EAST_1)
                .build();
    }
}
