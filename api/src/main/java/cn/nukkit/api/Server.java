package cn.nukkit.api;

import cn.nukkit.api.command.ConsoleCommandExecutorSource;
import cn.nukkit.api.event.EventManager;
import cn.nukkit.api.item.ItemStackBuilder;
import cn.nukkit.api.permission.PermissionManager;
import cn.nukkit.api.plugin.PluginManager;
import cn.nukkit.api.util.ConfigBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nonnull;

public interface Server {

    void broadcastMessage(String message);

    @Nonnull
    String getName();

    @Nonnull
    String getVersion();

    @Nonnull
    String getApiVersion();

    @Nonnull
    EventManager getEventManager();

    @Nonnull
    PluginManager getPluginManager();

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
}
