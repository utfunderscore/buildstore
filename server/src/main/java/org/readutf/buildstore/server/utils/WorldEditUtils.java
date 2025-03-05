package org.readutf.buildstore.server.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import java.util.Optional;
import org.bukkit.entity.Player;

public class WorldEditUtils {

    public static Optional<Region> getRegion(Player player) {
        try {
            BukkitPlayer adapt = BukkitAdapter.adapt(player);

            return Optional.ofNullable(adapt.getSelection());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
