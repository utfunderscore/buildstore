package org.readutf.buildstore.s3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.readutf.buildstore.api.Build;
import org.readutf.buildstore.api.BuildMeta;
import org.readutf.buildstore.api.BuildStore;
import org.readutf.buildstore.api.exception.BuildException;
import org.readutf.buildstore.api.utils.ByteStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3BuildStore implements BuildStore {

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
    public boolean exists(String name) throws BuildException {
        return false;
    }

    @Override
    public Collection<BuildMeta> getBuilds() throws BuildException {
        return List.of();
    }

    @Override
    public Build loadBuild(String buildName, Integer version) throws BuildException {
        return null;
    }

    @Override
    public @NonNull List<BuildMeta> getHistory(String name) throws BuildException {
        return getBuildHistory(name);
    }

    @Override
    public void saveBuild(Build build) throws BuildException {
        if (!build.buildMeta().name().matches(NAME_REGEX)) {
            throw new BuildException("Invalid build name, use only alphanumeric characters and underscores.");
        }

        List<BuildMeta> history = getBuildHistory(build.buildMeta().name());

        try {
            client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(getObjectName(build) + ".schem")
                            .build(), RequestBody.fromBytes(build.spongeSchematicData())
            );

            client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(getObjectName(build) + ".meta")
                            .build(), RequestBody.fromBytes(BuildMeta.serialise(build.buildMeta()))
            );

            history.add(build.buildMeta());
            client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(build.buildMeta().name() + ".history")
                            .build(), RequestBody.fromBytes(serializeBuildHistory(history))
            );

        } catch (Exception e) {
            logger.error("An error occurred while uploading a build to s3", e);
            throw new BuildException("An error occurred while uploading your build.");
        }

    }

    private List<BuildMeta> getBuildHistory(String name) {
        List<BuildMeta> history;
        try {
            byte[] byteArray = client.getObjectAsBytes(GetObjectRequest.builder().bucket(bucket).key(
                    name + ".history").build()).asByteArray();
            history = deserializeBuildHistory(byteArray);
        } catch (Exception e) {
            history = new ArrayList<>();
        }
        return history;
    }

    public byte[] serializeBuildHistory(List<BuildMeta> buildMetas) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteStreamUtils.writeInt(out, buildMetas.size());
        for (BuildMeta meta : buildMetas) {
            byte[] serialised = BuildMeta.serialise(meta);
            out.write(serialised, 0, serialised.length);
        }
        return out.toByteArray();
    }

    public List<BuildMeta> deserializeBuildHistory(byte[] data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        int size = ByteStreamUtils.readInt(in);
        List<BuildMeta> buildHistory = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            buildHistory.add(BuildMeta.deserialise(in));
        }
        return buildHistory;
    }

    public String getObjectName(Build build) {
        return build.buildMeta().name() + "-" + build.buildMeta().version();
    }

}
