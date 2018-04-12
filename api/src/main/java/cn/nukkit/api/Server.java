/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.api;

import cn.nukkit.api.command.sender.ConsoleCommandSender;
import cn.nukkit.api.event.EventManager;
import cn.nukkit.api.item.ItemInstanceBuilder;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.LevelCreator;
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
