package com.nukkitx.api;

import com.nukkitx.api.block.BlockStateBuilder;
import com.nukkitx.api.command.CommandManager;
import com.nukkitx.api.command.sender.ConsoleCommandSender;
import com.nukkitx.api.event.EventManager;
import com.nukkitx.api.item.ItemStackBuilder;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.LevelCreator;
import com.nukkitx.api.level.chunk.generator.ChunkGeneratorRegistry;
import com.nukkitx.api.locale.LocaleManager;
import com.nukkitx.api.permission.Abilities;
import com.nukkitx.api.permission.PermissionManager;
import com.nukkitx.api.plugin.PluginManager;
import com.nukkitx.api.scheduler.NukkitScheduler;
import com.nukkitx.api.util.ConfigBuilder;
import com.nukkitx.api.util.SemVer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
public interface Server {

    void broadcastMessage(String message);

    @Nonnull
    String getName();

    @Nonnull
    SemVer getMinecraftVersion();

    @Nonnull
    SemVer getApiVersion();

    @Nonnull
    Level getDefaultLevel();

    @Nonnull
    Abilities getDefaultAbilities();

    @Nonnull
    String getNukkitVersion();

    @Nonnull
    EventManager getEventManager();

    @Nonnull
    CommandManager getCommandManager();

    @Nonnull
    PluginManager getPluginManager();

    @Nonnull
    LocaleManager getLocaleManager();

    NukkitScheduler getScheduler();

    @Nonnull
    ConsoleCommandSender getConsoleCommandSender();

    @Nonnull
    Configuration getConfiguration();

    @Nonnull
    Banlist getBanlist();

    @Nonnull
    Whitelist getWhitelist();

    @Nonnull
    Operators getOperators();

    @Nonnull
    ItemStackBuilder itemInstanceBuilder();

    @Nonnull
    BlockStateBuilder blockStateBuilder();

    @Nonnull
    ConfigBuilder createConfigBuilder();

    @Nonnull
    PermissionManager getPermissionManager();

    Optional<Player> getPlayer(String name);

    Optional<Player> getPlayerExact(String name);

    void shutdown();

    void shutdown(String reason);

    @Nonnull
    Collection<Player> getOnlinePlayers();

    @Nonnull
    CompletableFuture<Level> loadLevel(LevelCreator creator);

    @Nonnull
    ChunkGeneratorRegistry getGeneratorRegistry();

    boolean isWhitelisted(String name);

    boolean isWhitelisted(Player player);

    boolean isWhitelisted(UUID uuid);
}
