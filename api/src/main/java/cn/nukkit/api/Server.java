package cn.nukkit.api;

import cn.nukkit.api.command.sender.ConsoleCommandSender;
import cn.nukkit.api.event.EventManager;
import cn.nukkit.api.item.ItemInstanceBuilder;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.permission.PermissionManager;
import cn.nukkit.api.plugin.PluginManager;
import cn.nukkit.api.scheduler.NukkitScheduler;
import cn.nukkit.api.util.Config;
import cn.nukkit.api.util.ConfigBuilder;
import cn.nukkit.api.util.GameMode;
import cn.nukkit.api.util.SemVer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

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
    SemVer getNukkitVersion();

    @Nonnull
    EventManager getEventManager();

    @Nonnull
    PluginManager getPluginManager();

    NukkitScheduler getScheduler();

    @Nonnull
    ConsoleCommandSender getConsoleCommandSender();

    @Nonnull
    Configuration getConfiguration();

    @Nonnull
    Whitelist getWhitelist();

    @Nonnull
    ConfigBuilder getConfigBuilder();

    @Nonnull
    ItemInstanceBuilder getItemStackBuilder();

    @Nonnull
    PermissionManager getPermissionManager();

    Player getPlayer(String name);

    Player getPlayerExact(String name);

    void shutdown();

    void forceShutdown();

    @Nonnull
    GameMode getDefaultGameMode();

    @Nonnull
    Config getConfig();

    @Nullable
    Object getConfig(String variable);

    @Nullable
    Object getConfig(String variable, Object defaultValue);

    @Nonnull
    Map<UUID, Player> getOnlinePlayers();
}
