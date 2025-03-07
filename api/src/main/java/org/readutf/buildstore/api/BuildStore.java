package org.readutf.buildstore.api;

import java.util.List;
import org.jspecify.annotations.NonNull;
import org.readutf.buildstore.api.exception.BuildException;

/**
 * Represents a store for builds, either backed by a database, filesystem, or other storage mechanism.
 * Implementations are free to block the calling thread, or perform operations asynchronously.
 */
public class BuildStore {

    private @NonNull final BuildDataStore buildDataStore;
    private @NonNull final BuildMetaStore buildMetaStore;

    public BuildStore(@NonNull BuildDataStore buildDataStore, @NonNull BuildMetaStore buildMetaStore) {
        this.buildDataStore = buildDataStore;
        this.buildMetaStore = buildMetaStore;
    }

    public boolean exists(String buildName) throws BuildException {
        return buildMetaStore.exists(buildName);
    }

    public void save(BuildMeta meta, byte[] data) throws BuildException {
        buildMetaStore.save(meta);
        buildDataStore.save(new BuildData(meta.name(), meta.version(), data));
    }

    public List<BuildMeta> getHistory(String buildName) throws BuildException {
        return buildMetaStore.getBuildMetas(buildName);
    }

    public Build loadLatest(String buildName) throws BuildException{
        BuildMeta metas = buildMetaStore.getLatest(buildName);
        BuildData data = buildDataStore.load(metas.name(), metas.version());
        return new Build(metas, data);
    }

    public Build load(String buildName, int buildVersion) throws BuildException {
        BuildMeta meta = buildMetaStore.getBuildMeta(buildName, buildVersion);
        BuildData data = buildDataStore.load(buildName, buildVersion);
        return new Build(meta, data);
    }

}
