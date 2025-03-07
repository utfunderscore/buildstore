package org.readutf.buildstore.s3;

import java.net.URI;
import org.jspecify.annotations.NonNull;
import org.readutf.buildstore.api.BuildData;
import org.readutf.buildstore.api.BuildDataStore;
import org.readutf.buildstore.api.exception.BuildException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3BuildStore implements BuildDataStore {

    private static final Logger logger = LoggerFactory.getLogger(S3BuildStore.class);
    private static final String NAME_REGEX = "^[a-zA-Z0-9_]{1,32}$";

    private @NonNull final String bucket;
    private @NonNull final S3Client client;

    public S3BuildStore(@NonNull AwsBasicCredentials credentials, @NonNull Region region, @NonNull String bucket) {
        client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        this.bucket = bucket;

        client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
    }

    public S3BuildStore(@NonNull AwsBasicCredentials credentials, URI endpointOverride, @NonNull Region region, @NonNull String bucket) {
        S3Configuration serviceConfiguration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        client = S3Client.builder()
                .region(region)
                .endpointOverride(endpointOverride)
                .serviceConfiguration(serviceConfiguration)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        this.bucket = bucket;

        client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
    }


    @Override
    public BuildData load(String name, int version) throws BuildException {
        try {
            byte[] byteArray = client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(name + "/" + version + ".schem")
                            .build(), ResponseTransformer.toBytes()).asByteArray();

            return new BuildData(name, version, byteArray);
        } catch (Exception e) {
            logger.error("Failed to load build data", e);
            throw new BuildException("Failed to load build data", e, "An error occurred while loading build data");
        }
    }

    @Override
    public void save(BuildData data) throws BuildException {
        try {
            client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(data.name() + "/" + data.version() + ".schem")
                            .build(), RequestBody.fromBytes(data.schematicBytes())
            );
        } catch (Exception e) {
            logger.error("Failed to save build data", e);
            throw new BuildException("Failed to save build data", e, "An error occurred while saving build data");
        }
    }

}
