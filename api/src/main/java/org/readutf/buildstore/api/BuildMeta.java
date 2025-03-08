package org.readutf.buildstore.api;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.jspecify.annotations.NonNull;

public record BuildMeta(@NonNull String name, int version, @NonNull String description, @NonNull List<String> labels,
                        @NonNull UUID savedBy, LocalDateTime savedAt) {

    public String getId() {
        return name + ":" + version;
    }

    private static final byte FORMAT_VERSION = 1;

//    public static byte[] serialise(BuildMeta buildMeta) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        outputStream.write(FORMAT_VERSION);
//        ByteStreamUtils.writeString(outputStream, buildMeta.name());
//        ByteStreamUtils.writeInt(outputStream, buildMeta.version());
//        ByteStreamUtils.writeString(outputStream, buildMeta.description());
//        ByteStreamUtils.writeInt(outputStream, buildMeta.labels().size());
//        for (String label : buildMeta.labels()) {
//            ByteStreamUtils.writeString(outputStream, label);
//        }
//        ByteStreamUtils.writeUUID(outputStream, buildMeta.savedBy());
//        ByteStreamUtils.writeLong(outputStream, buildMeta.savedAt());
//        return outputStream.toByteArray();
//    }
//
//    public static BuildMeta deserialise(ByteArrayInputStream inputStream) {
//
//        byte ignored = (byte) inputStream.read();
//        String name = ByteStreamUtils.readString(inputStream);
//        int version = ByteStreamUtils.readInt(inputStream);
//        String description = ByteStreamUtils.readString(inputStream);
//        int labelCount = ByteStreamUtils.readInt(inputStream);
//        ArrayList<String> labels = new ArrayList<>(labelCount);
//        for (int i = 0; i < labelCount; i++) {
//            labels.add(ByteStreamUtils.readString(inputStream));
//        }
//        UUID savedBy = ByteStreamUtils.readUUID(inputStream);
//        long savedAt = ByteStreamUtils.readLong(inputStream);
//
//        return new BuildMeta(name, version, description, labels, savedBy, savedAt);
//    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BuildMeta buildMeta)) return false;
        return version == buildMeta.version &&
                Objects.equals(name, buildMeta.name) &&
                Objects.equals(savedBy, buildMeta.savedBy) &&
                Objects.equals(description, buildMeta.description) &&
                Objects.equals(labels, buildMeta.labels) &&
                Duration.between(savedAt, buildMeta.savedAt).abs().getSeconds() < 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version, description, labels, savedBy, savedAt);
    }
}

