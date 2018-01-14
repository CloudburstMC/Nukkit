package cn.nukkit.api;

import cn.nukkit.api.command.source.ConsoleCommandExecutorSource;
import cn.nukkit.api.event.EventManager;
import cn.nukkit.api.item.ItemStackBuilder;
import cn.nukkit.api.permission.PermissionManager;
import cn.nukkit.api.plugin.PluginManager;
import cn.nukkit.api.scheduler.NukkitScheduler;
import cn.nukkit.api.util.Config;
import cn.nukkit.api.util.ConfigBuilder;
import cn.nukkit.api.util.SemVer;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    SemVer getNukkitVersion();

    @Nonnull
    EventManager getEventManager();

    @Nonnull
    PluginManager getPluginManager();

    NukkitScheduler getScheduler();

    @Nonnull
    ConsoleCommandExecutorSource getConsoleCommandExecutorSource();

    @Nonnull
    ObjectMapper getJsonMapper();

    @Nonnull
    ObjectMapper getYamlMapper();

    @Nonnull
    ObjectMapper getPropertiesMapper();

    @Nonnull
    ServerProperties getServerProperties();

    @Nonnull
    Whitelist getWhitelist();

    @Nonnull
    ConfigBuilder getConfigBuilder();

    @Nonnull
    ItemStackBuilder getItemStackBuilder();

    @Nonnull
    PermissionManager getPermissionManager();

    Player getPlayer(String name);

    Player getPlayerExact(String name);

    void shutdown();

    void forceShutdown();

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
