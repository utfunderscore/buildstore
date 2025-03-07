package org.readutf.buildstore.server.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import static net.kyori.adventure.text.format.NamedTextColor.BLUE;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.readutf.buildstore.api.Build;
import org.readutf.buildstore.api.BuildMeta;
import org.readutf.buildstore.api.BuildStore;
import org.readutf.buildstore.api.exception.BuildException;
import org.readutf.buildstore.server.utils.PlayerFetcher;
import org.readutf.buildstore.server.build.PartialBuildMeta;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Switch;

public class BuildCommand {

    private @NonNull final Logger logger;
    private @NotNull final BuildStore buildStore;
    private @NonNull final Map<String, PartialBuildMeta> partialBuilds = new HashMap<>();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE d yyyy");
    private static final ExecutorService uuidExecutor = Executors.newSingleThreadExecutor();

    public BuildCommand(@NonNull Logger logger, @NotNull BuildStore buildStore) {
        this.logger = logger;
        this.buildStore = buildStore;
    }

    @Command({"build", "build help"})
    public void help(Player player) {
        player.sendMessage(Component.text("Build commands:").decorate(TextDecoration.BOLD));
        player.sendMessage(Component.text("/build create").color(GRAY).append(
                Component.text(" - Create a new build").color(WHITE)));
        player.sendMessage(Component.text("/build setdescription <description>").color(GRAY).append(
                Component.text(" - Set the description of the build").color(WHITE)));
        player.sendMessage(Component.text("/build addlabel <labels>").color(GRAY).append(
                Component.text(" - Add labels to the build").color(WHITE)));
        player.sendMessage(Component.text("/build removelabel <labels>").color(GRAY).append(
                Component.text(" - Remove labels from the build").color(WHITE)));
        player.sendMessage(Component.text("/build save").color(GRAY).append(
                Component.text(" - Save the build").color(WHITE)));
    }

    @Command({"build info"})
    public void info(Player player, String name) {
        PartialBuildMeta partialBuildMeta = partialBuilds.get(name);

        if (partialBuildMeta != null) {
            player.sendMessage(
                    Component.text("Build info for " + name).color(BLUE).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text("State: ").color(GRAY).append(
                    Component.text("Local").color(NamedTextColor.GREEN)));
            player.sendMessage(Component.text("Version: ").color(GRAY).append(
                    Component.text(partialBuildMeta.getVersion()).color(WHITE)));
            if (partialBuildMeta.getDescription() != null) {
                player.sendMessage(Component.text("Description: ").color(GRAY).append(
                        Component.text(partialBuildMeta.getDescription()).color(WHITE)));
            } else {
                player.sendMessage(Component.text("Description: ").color(GRAY).append(
                        Component.text("Not set").color(NamedTextColor.RED)));
            }
            if (partialBuildMeta.getLabels() != null && !partialBuildMeta.getLabels().isEmpty()) {
                player.sendMessage(Component.text("Labels: ").color(GRAY).append(
                        Component.text(String.join(", ", partialBuildMeta.getLabels())).color(WHITE)));
            } else {
                player.sendMessage(Component.text("Labels: ").color(GRAY).append(
                        Component.text("Not set").color(NamedTextColor.RED)));
            }
            player.sendMessage(Component.text("Use /build save to save your build").color(GRAY));
            return;
        }

        Build latestBuildData;
        try {
            latestBuildData = buildStore.loadLatest(name);
        } catch (BuildException e) {
            player.sendMessage(Component.text(e.getUserMessage()).color(NamedTextColor.RED));
            return;
        }
        if (latestBuildData != null) {
            BuildMeta buildMeta = latestBuildData.buildMeta();
            player.sendMessage(
                    Component.text("Build info for " + name).color(BLUE).decorate(TextDecoration.BOLD));
            player.sendMessage(Component.text("State ").color(GRAY).append(
                    Component.text("Saved").color(NamedTextColor.GREEN)));
            player.sendMessage(Component.text("Version: ").color(GRAY).append(Component.text(
                    buildMeta.version()).color(WHITE)));
            player.sendMessage(Component.text("Description: ").color(GRAY).append(Component.text(
                    buildMeta.description()).color(WHITE)));
            if (buildMeta.labels().isEmpty()) {
                player.sendMessage(Component.text("Labels: ").color(GRAY).append(
                        Component.text("None").color(WHITE)));
            } else {
                player.sendMessage(Component.text("Labels: ").color(GRAY).append(
                        Component.text(String.join(", ", buildMeta.labels())).color(
                                WHITE)));
            }
            player.sendMessage(
                    Component.text("Saved by: ").color(GRAY).append(Component.text(PlayerFetcher.getName(
                            buildMeta.savedBy()).orElse("Unknown")).color(WHITE)));
            player.sendMessage(Component.text("Saved at: ").color(GRAY).append(
                    Component.text(DATE_FORMAT.format(buildMeta.savedAt())).color(
                            WHITE)));
            return;
        }

        player.sendMessage(Component.text("No build with that name found").color(NamedTextColor.RED));

    }

    @Command("build create <name>")
    public void createBuild(Player player, String name) {
        try {
            if (buildStore.exists(name)) {
                player.sendMessage(Component.text("A build with that name already exists").color(NamedTextColor.RED));
                return;
            }
        } catch (BuildException e) {
            player.sendMessage(Component.text(e.getUserMessage()).color(NamedTextColor.RED));
            return;
        }


        if (partialBuilds.containsKey(name)) {
            player.sendMessage(Component.text("Build already loaded locally.").color(NamedTextColor.RED));
            return;
        }

        partialBuilds.put(name, new PartialBuildMeta(name, 1, "Not set", Collections.emptyList()));
        player.sendMessage(
                Component.text("Build created! Use /build to view commands to edit and save your build.").color(
                        NamedTextColor.GREEN));
    }

    @Command("build load <name>")
    public void load(Player player, String name, @Default("latest") String version, @Switch("force") @Default("false") Boolean force) {
        if (!force && partialBuilds.containsKey(name)) {
            player.sendMessage(Component.text().content(
                    "Build already loaded locally. Use /build load <name> --force to override.").color(
                    NamedTextColor.RED));
            return;
        }
        Build buildData;

        try {
            if (version.equalsIgnoreCase("latest")) {
                buildData = buildStore.loadLatest(name);
            } else {
                buildData = buildStore.load(name, Integer.parseInt(version));
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid version number").color(NamedTextColor.RED));
            return;
        } catch (BuildException e) {
            player.sendMessage(Component.text(e.getUserMessage()).color(NamedTextColor.RED));
            return;
        }

        if (buildData == null) {
            player.sendMessage(Component.text("No build with that name found").color(NamedTextColor.RED));
            return;
        }

        byte[] schematicData = buildData.buildData().schematicBytes();

        try (ClipboardReader reader = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getReader(
                new ByteArrayInputStream(schematicData))) {
            Clipboard clipboard = reader.read();

            BukkitPlayer adapt = BukkitAdapter.adapt(player);
            adapt.getSession().setClipboard(new ClipboardHolder(clipboard));

            partialBuilds.put(
                    name, new PartialBuildMeta(
                            name, buildData.buildMeta().version() + 1, buildData.buildMeta().description(),
                            new ArrayList<>(
                                    buildData.buildMeta().labels())
                    )
            );

            player.sendMessage(
                    Component.text("The build has been loaded into your clipboard.").color(NamedTextColor.GREEN));
        } catch (IOException e) {
            player.sendMessage(Component.text(e.getMessage()));
        }

    }

    @Command("build save <name>")
    public void saveBuild(Player player, String name) {
        PartialBuildMeta buildMeta = checkEditable(player, name);
        if (buildMeta == null) return;

        BukkitPlayer adapt = BukkitAdapter.adapt(player);

        Clipboard clipboard = adapt.getSession().getClipboard().getClipboard();
        if (clipboard == null) {
            player.sendMessage(Component.text("You must have a build loaded in your clipboard to save it").color(
                    NamedTextColor.RED));
            return;
        }

        if (buildMeta.getDescription() == null) {
            player.sendMessage(Component.text("You must set a description for your build before saving it").color(
                    NamedTextColor.RED));
            return;
        }

        if (buildMeta.getLabels() == null) {
            player.sendMessage(
                    Component.text("You must set labels for your build before saving it").color(NamedTextColor.RED));
            return;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getWriter(out)) {
            writer.write(clipboard);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to write schematic schematicBytes", e);
            player.sendMessage(Component.text(e.getMessage()).color(NamedTextColor.RED));
            return;
        }

        try {
            buildStore.save(
                    new BuildMeta(
                            buildMeta.getName(), buildMeta.getVersion(), buildMeta.getDescription(),
                            new ArrayList<>(buildMeta.getLabels()), player.getUniqueId(), LocalDateTime.now()
                    ), out.toByteArray()
            );
        } catch (BuildException e) {
            logger.log(Level.SEVERE, "Failed to save build", e);
            player.sendMessage(Component.text(e.getUserMessage()).color(NamedTextColor.RED));
            return;
        }
        player.sendMessage(Component.text(
                "%s v%d has been saved using your clipboard.".formatted(name, buildMeta.getVersion())).color(
                NamedTextColor.GREEN));
    }

    @Command("build setdescription <name> <description>")
    public void setDescription(Player player, String name, String description) {
        PartialBuildMeta buildMeta = checkEditable(player, name);
        if (buildMeta == null) return;

        buildMeta.setDescription(description);
        player.sendMessage(Component.text("Description set!").color(NamedTextColor.GREEN));
    }

    @Command("build addlabel <name> <label>")
    public void addLabel(Player player, String name, String label) {
        PartialBuildMeta buildMeta = checkEditable(player, name);
        if (buildMeta == null) return;

        if (buildMeta.getLabels() == null) {
            buildMeta.setLabels(new HashSet<>());
        }
        buildMeta.getLabels().add(label);
        player.sendMessage(Component.text("Labels added!").color(NamedTextColor.GREEN));
    }

    @Command("build removelabel <name> <label>")
    public void removeLabel(Player player, String name, String label) {
        PartialBuildMeta buildMeta = checkEditable(player, name);
        if (buildMeta == null) return;

        if (buildMeta.getLabels() == null) {
            player.sendMessage(Component.text("No labels to remove").color(NamedTextColor.RED));
            return;
        }
        buildMeta.getLabels().remove(label);
        player.sendMessage(Component.text("Label removed!").color(NamedTextColor.GREEN));
    }

    @Command("build setversion <name> <version>")
    public void setVersion(Player player, String name, int version) {
        PartialBuildMeta buildMeta = checkEditable(player, name);
        if (buildMeta == null) return;

        buildMeta.setVersion(version);
        player.sendMessage(Component.text("Version set!").color(NamedTextColor.GREEN));
    }

    @Command("build history <name>")
    public void history(Player player, String name) {
        final List<BuildMeta> buildMeta;
        try {
            buildMeta = buildStore.getHistory(name).stream().sorted(
                    Comparator.comparingInt(BuildMeta::version).reversed()).toList();
        } catch (BuildException e) {
            logger.log(Level.SEVERE, "Failed to fetch build history", e);
            player.sendMessage(Component.text(e.getUserMessage()).color(NamedTextColor.RED));
            return;
        }

        if (buildMeta.isEmpty()) {
            player.sendMessage(Component.text("No builds found").color(NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("Build history for " + name).color(NamedTextColor.GREEN));
        CompletableFuture.runAsync(
                () -> {
                    for (BuildMeta meta : buildMeta) {

                        String playerName = PlayerFetcher.getName(meta.savedBy()).orElse("Unknown");
                        String date = DATE_FORMAT.format(meta.savedAt());

                        Component line = Component.text(" * ").color(GRAY)
                                .append(Component.text(meta.version()).color(NamedTextColor.AQUA))
                                .append(Component.text(" saved by ").color(WHITE))
                                .append(Component.text(playerName).color(NamedTextColor.AQUA))
                                .append(Component.text(" on ").color(WHITE))
                                .append(Component.text(date).color(NamedTextColor.AQUA));

                        player.sendMessage(line);
                    }
                }, uuidExecutor
        ).exceptionally(e -> {
            logger.log(Level.SEVERE, "Failed to fetch player names", e);
            return null;
        });

    }

    private @Nullable PartialBuildMeta checkEditable(Player player, String name) {
        PartialBuildMeta buildMeta = partialBuilds.get(name);
        if (buildMeta == null) {
            try {
                if (buildStore.exists(name)) {
                    player.sendMessage(Component.text("You must use /build load <name> before you can edit it.").color(
                            NamedTextColor.RED));
                    return null;
                }
            } catch (BuildException e) {
                logger.log(Level.SEVERE, "Failed to check if build exists", e);
                player.sendMessage(Component.text(e.getUserMessage()).color(NamedTextColor.RED));
                return null;
            }

            player.sendMessage(Component.text(
                    "No build with that name has been created, start by using /build create <name>").color(
                    NamedTextColor.RED));
            return null;
        }
        return buildMeta;
    }

    private BlockArrayClipboard createSchematicData(Region region, World world) {
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                world, region, clipboard, region.getMinimumPoint()
        );
        Operations.complete(forwardExtentCopy);
        return clipboard;
    }

}
