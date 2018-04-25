package cn.nukkit.api;

import cn.nukkit.api.command.sender.ConsoleCommandSender;
import cn.nukkit.api.event.EventManager;
import cn.nukkit.api.item.ItemInstanceBuilder;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.LevelCreator;
import cn.nukkit.api.locale.LocaleManager;
import cn.nukkit.api.permission.Abilities;
import cn.nukkit.api.permission.PermissionManager;
import cn.nukkit.api.plugin.PluginManager;
import cn.nukkit.api.scheduler.NukkitScheduler;
import cn.nukkit.api.util.ConfigBuilder;
import cn.nukkit.api.util.SemVer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Optional;
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
    ItemInstanceBuilder itemInstanceBuilder();

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
}
