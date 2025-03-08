package org.readutf.buildstore.server;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.readutf.buildstore.api.BuildDataStore;
import org.readutf.buildstore.api.BuildMeta;
import org.readutf.buildstore.api.BuildMetaStore;
import org.readutf.buildstore.api.BuildStore;
import org.readutf.buildstore.s3.S3BuildStore;
import org.readutf.buildstore.server.commands.BuildCommand;
import org.readutf.buildstore.server.commands.utils.BuildExceptionHandler;
import org.readutf.buildstore.sql.SQLBuildDataStore;
import org.readutf.buildstore.sql.SQLBuildMetaStore;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;

public class BuildStorePlugin extends JavaPlugin {

    private final @NotNull Logger logger = getLogger();

    @Override
    public void onEnable() {

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        BuildMetaStore buildMetaStore;
        BuildDataStore buildDataStore;

        ConfigurationSection buildMetaSection = getConfig().getConfigurationSection("meta");
        ConfigurationSection buildDataSection = getConfig().getConfigurationSection("schematics");
        if(buildMetaSection == null || buildDataSection == null) {
            logger.severe("Invalid configuration. Build meta or data store is not configured.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        buildMetaStore = getBuildMetaStore(buildMetaSection);
        if (buildMetaStore == null) return;

        buildDataStore = getBuildDataStore(buildDataSection);
        if (buildDataStore == null) return;

        Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(this).exceptionHandler(new BuildExceptionHandler()).build();
        lamp.register(new BuildCommand(createLogger(BuildCommand.class), new BuildStore(buildDataStore, buildMetaStore)));
    }

    private @Nullable BuildDataStore getBuildDataStore(ConfigurationSection buildDataSection) {
        BuildDataStore buildDataStore;
        if(buildDataSection.getBoolean("s3.enabled")) {
            String endpointURI = buildDataSection.getString("s3.endpoint");
            URI endpoint = endpointURI == null ? null : URI.create(endpointURI);
            AwsBasicCredentials credentials = AwsBasicCredentials.create(
                    buildDataSection.getString("s3.access-key"),
                    buildDataSection.getString("s3.secret-key")
            );
            Region region = Region.of(buildDataSection.getString("s3.region"));
            String bucket = buildDataSection.getString("s3.bucket");
            boolean pathStyleAccess = buildDataSection.getBoolean("s3.path-style-access", false);
            if(bucket == null) {
                logger.severe("Invalid s3 configuration. Please provide a valid bucket.");
                getServer().getPluginManager().disablePlugin(this);
                return null;
            }
            if(endpoint != null) {
                buildDataStore = new S3BuildStore(credentials, endpoint, region, bucket, pathStyleAccess);
            } else {
                buildDataStore = new S3BuildStore(credentials, region, bucket, pathStyleAccess);
            }
        } else if(buildDataSection.getBoolean("sql.enabled")) {
            try {
                JdbcConnectionSource connectionSource = new JdbcConnectionSource(
                        buildDataSection.getString("sql.url"),
                        buildDataSection.getString("sql.username"),
                        buildDataSection.getString("sql.password")
                );
                buildDataStore = new SQLBuildDataStore(connectionSource);
            } catch (SQLException e) {
                logger.severe("Invalid configuration. Please provide a valid configuration for the build store.");
                getServer().getPluginManager().disablePlugin(this);
                return null;
            }
        } else {
            logger.severe("At least one build data store must be enabled.");
            getServer().getPluginManager().disablePlugin(this);
            return null;
        }
        return buildDataStore;
    }

    private @Nullable BuildMetaStore getBuildMetaStore(ConfigurationSection buildMetaSection) {
        BuildMetaStore buildMetaStore;
        if(buildMetaSection.getBoolean("sql.enabled")) {
            try {
                JdbcConnectionSource connectionSource = new JdbcConnectionSource(
                        buildMetaSection.getString("sql.url"),
                        buildMetaSection.getString("sql.username"),
                        buildMetaSection.getString("sql.password")
                );
                buildMetaStore = new SQLBuildMetaStore(connectionSource);
            } catch (SQLException e) {
                logger.severe("Invalid configuration. Please provide a valid configuration for the build store.");
                getServer().getPluginManager().disablePlugin(this);
                return null;
            }
        } else {
            logger.severe("At least one build meta store must be enabled.");
            getServer().getPluginManager().disablePlugin(this);
            return null;
        }
        return buildMetaStore;
    }

    public Logger createLogger(Class<?> aClass) {
        Logger logger = Logger.getLogger(aClass.getSimpleName());
        logger.setParent(this.logger);
        return logger;
    }

}
