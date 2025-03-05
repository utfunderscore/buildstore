package org.readutf.buildstore.server;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.readutf.buildstore.api.BuildStore;
import org.readutf.buildstore.api.simple.SimpleBuildStore;
import org.readutf.buildstore.server.commands.BuildCommand;
import org.readutf.buildstore.server.commands.utils.BuildExceptionHandler;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

public class BuildStorePlugin extends JavaPlugin {

    private final @NotNull Logger logger = getLogger();

    @Override
    public void onEnable() {

        File dataFolder = getDataFolder();


        BuildStore buildStore;
        try {
            buildStore = new SimpleBuildStore(createLogger(SimpleBuildStore.class), new Gson(), dataFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(this).exceptionHandler(new BuildExceptionHandler()).build();
        lamp.register(new BuildCommand(createLogger(BuildCommand.class), buildStore));
    }

    public Logger createLogger(Class<?> aClass) {
        Logger logger = Logger.getLogger(aClass.getSimpleName());
        logger.setParent(this.logger);
        return logger;
    }

}
