package org.readutf.buildstore.server.build;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.readutf.buildstore.api.BuildMeta;

public final class PartialBuildMeta {
    private @NonNull final String name;
    private int version;
    private @Nullable String description;
    private @Nullable HashSet<String> labels;

    public PartialBuildMeta(@NonNull String name, int version, @Nullable String description, @Nullable List<String> labels) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.labels = labels == null ? null : new HashSet<>(labels);
    }

    public static PartialBuildMeta fromBuildMeta(BuildMeta buildMeta) {
        return new PartialBuildMeta(buildMeta.name(), buildMeta.version(), buildMeta.description(), buildMeta.labels());
    }

    public @NonNull String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public @Nullable HashSet<String> getLabels() {
        return labels;
    }

    public void setLabels(@Nullable HashSet<String> labels) {
        this.labels = labels;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PartialBuildMeta) obj;
        return Objects.equals(this.name, that.name) &&
                this.version == that.version &&
                Objects.equals(this.description, that.description) &&
                Objects.equals(this.labels, that.labels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version, description, labels);
    }

    @Override
    public String toString() {
        return "PartialBuildMeta[" +
                "name=" + name + ", " +
                "version=" + version + ", " +
                "description=" + description + ", " +
                "labels=" + labels + ']';
    }


}
