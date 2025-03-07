package org.readutf.buildstore.api.simple;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.jspecify.annotations.NonNull;
import org.readutf.buildstore.api.BuildMeta;
import org.readutf.buildstore.api.BuildMetaStore;
import org.readutf.buildstore.api.BuildStore;
import org.readutf.buildstore.api.exception.BuildException;

public class SimpleBuildMetaStore implements BuildMetaStore {

    private final Logger logger;
    private @NonNull final File buildsDirectory;
    private @NonNull final File metaTrackerFile;
    private @NonNull final Map<String, BuildMeta> builds;

    public SimpleBuildMetaStore(@NonNull Logger logger, @NonNull Gson gson, @NonNull File buildsDirectory) throws IOException, JsonParseException {
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
    public BuildMeta getBuildMeta(String name, int version) throws BuildException {
        return null;
    }

    @Override
    public List<BuildMeta> getBuildMetas(String name) throws BuildException {
        return List.of();
    }

    @Override
    public boolean exists(String buildName) throws BuildException {
        return false;
    }

    @Override
    public void save(BuildMeta meta) throws BuildException {

    }

    @Override
    public BuildMeta getLatest(String buildName) throws BuildException {
        return null;
    }
}
