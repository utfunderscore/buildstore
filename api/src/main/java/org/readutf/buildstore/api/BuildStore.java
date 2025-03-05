package org.readutf.buildstore.api;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.readutf.buildstore.api.exception.BuildException;

/**
 * Represents a store for builds, either backed by a database, filesystem, or other storage mechanism.
 * Implementations are free to block the calling thread, or perform operations asynchronously.
 */
public interface BuildStore {

    boolean exists(String name) throws BuildException;

    Collection<BuildMeta> getBuilds() throws BuildException;

    Build loadBuild(String buildName, Integer version) throws BuildException;

    void saveBuild(Build build) throws BuildException;

    default Build loadBuild(BuildMeta buildMeta) throws BuildException {
        return loadBuild(buildMeta.name(), buildMeta.version());
    }

    default @NonNull BuildMeta getLatestBuild(String name) throws BuildException {

        return getBuilds().stream()
                .filter(meta -> meta.name().equalsIgnoreCase(name))
                .max(Comparator.comparingInt(BuildMeta::version)).orElseThrow(() -> new BuildException("No builds found for " + name));
    }

    default @NonNull List<BuildMeta> getHistory(String name) throws BuildException {
        return getBuilds().stream()
                .filter(meta -> meta.name().equalsIgnoreCase(name))
                .sorted(Comparator.comparingLong(BuildMeta::savedAt).reversed())
                .toList();
    }

    default @NonNull List<BuildMeta> getBuildsWithTag(String name) throws BuildException {
        return getBuilds().stream()
                .filter(meta -> meta.labels().contains(name))
                .toList();
    }



}
