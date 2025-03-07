package org.readutf.buildstore.server;

import java.net.URI;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.readutf.buildstore.s3.S3BuildStore;
import org.readutf.buildstore.server.commands.BuildCommand;
import org.readutf.buildstore.server.commands.utils.BuildExceptionHandler;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;

public class BuildStorePlugin extends JavaPlugin {

    private final @NotNull Logger logger = getLogger();

    @Override
    public void onEnable() {

//        BuildStore buildStore;
//        buildStore = new S3BuildStore(
//                AwsBasicCredentials.create("oysJPvEn8nT6H6iqLqqZ", "PovE1f3a4vxQ11taWmeibEEFZhbfxZjozqw8lpwp"),
//                URI.create("http://s3.utf.lol/"),
//                Region.of("auto"),
//                "builds"
//        );
//
//        Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(this).exceptionHandler(new BuildExceptionHandler()).build();
//        lamp.register(new BuildCommand(createLogger(BuildCommand.class), buildStore));
    }

    public Logger createLogger(Class<?> aClass) {
        Logger logger = Logger.getLogger(aClass.getSimpleName());
        logger.setParent(this.logger);
        return logger;
    }

}
