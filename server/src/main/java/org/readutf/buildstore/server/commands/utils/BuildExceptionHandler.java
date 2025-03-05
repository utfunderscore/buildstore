package org.readutf.buildstore.server.commands.utils;

import org.readutf.buildstore.api.exception.BuildException;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import static revxrsal.commands.bukkit.util.BukkitUtils.legacyColorize;

public class BuildExceptionHandler extends BukkitExceptionHandler {

    @HandleException
    public void onSomeCustomException(BuildException e, BukkitCommandActor actor) {
        actor.error(legacyColorize("&c" + e.getUserMessage()));
    }

}
