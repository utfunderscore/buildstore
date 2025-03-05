package org.readutf.buildstore.api.simple;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;
import org.jspecify.annotations.NonNull;
import org.readutf.buildstore.api.Build;
import org.readutf.buildstore.api.BuildMeta;
import org.readutf.buildstore.api.BuildStore;
import org.readutf.buildstore.api.exception.BuildException;

public class SimpleBuildStore implements BuildStore {

    private final Logger logger;
    private @NonNull final File buildsDirectory;
    private @NonNull final File metaTrackerFile;
    private @NonNull final Map<String, BuildMeta> builds;

    public SimpleBuildStore(@NonNull Logger logger, @NonNull Gson gson, @NonNull File buildsDirectory) throws IOException, JsonParseException {
        this.logger = logger;
        this.buildsDirectory = buildsDirectory;
        if (!buildsDirectory.exists()) buildsDirectory.mkdirs();
        this.metaTrackerFile = new File(buildsDirectory, "builds.json");
        if (!metaTrackerFile.exists()) {
            if (metaTrackerFile.createNewFile()) {
                logger.info("Created builds.json file");
                Files.writeString(metaTrackerFile.toPath(), "[]");
            }
        }
        builds = gson.fromJson(new JsonReader(new FileReader(metaTrackerFile)), new TypeToken<>() {
        });
    }

    @Override
    public boolean exists(String name) {
        return getBuilds().stream().anyMatch(meta -> meta.name().equalsIgnoreCase(name));
    }

    @Override
    public Collection<BuildMeta> getBuilds() {
        return builds.values();
    }

    @Override
    public Build loadBuild(String buildName, Integer version) throws BuildException {
        File buildFile = getBuildFile(buildName, version);
        try {
            byte[] schematicData = Files.readAllBytes(buildFile.toPath());
            BuildMeta metaData = builds.get(buildName + ":" + version);
            if(metaData == null) {
                throw new BuildException("Build %s v%d metadata could not be read".formatted(buildName, version));
            }
            return new Build(metaData, schematicData);
        } catch (IOException e) {
            throw new BuildException("Could not find a build with that name");
        }
    }

    @Override
    public void saveBuild(Build build) throws BuildException {
        if(builds.containsKey(build.buildMeta().getId())) {
            throw new BuildException("'%s' v%s already exists.".formatted(build.buildMeta().name(), build.buildMeta().version()));
        }

        builds.put(build.buildMeta().getId(), build.buildMeta());
        File buildFile = getBuildFile(build.buildMeta().name(), build.buildMeta().version());
        try {
            Files.write(buildFile.toPath(), build.spongeSchematicData());
        } catch (IOException e) {
            throw new BuildException("Failed to save build %s".formatted(build.buildMeta().name()));
        }
        saveBuildTree();
    }

    public void saveBuildTree() throws BuildException {
        try {
            Files.writeString(metaTrackerFile.toPath(), new Gson().toJson(builds));
        } catch (IOException e) {
            throw new BuildException("Failed to save build tree");
        }
    }

    public File getBuildFile(String name, int version) throws BuildException {
        File directory = getBuildDirectory(name, version);
        return new File(directory, "%s-v%d.schem".formatted(name, version));
    }

    private File getBuildDirectory(String name, int version) throws BuildException {
        File directory = new File(buildsDirectory, name);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                logger.info("Created directory for build %s".formatted(name));
            } else {
                throw new BuildException("Failed to create directory for build %s".formatted(name));
            }
        }
        return directory;
    }


}
