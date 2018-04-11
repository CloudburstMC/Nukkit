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

package cn.nukkit.server.permission;

import cn.nukkit.api.permission.Permission;
import cn.nukkit.server.NukkitServer;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class DefaultPermissions {
    public static final String ROOT = "nukkit";

    public static Permission registerPermission(Permission perm) {
        return registerPermission(perm, null);
    }

    public static Permission registerPermission(Permission perm, Permission parent) {
        if (parent != null) {
            parent.getChildren().put(perm.getName(), true);
        }
        NukkitServer.getInstance().getPermissionManager().addPermission(perm);

        return NukkitServer.getInstance().getPermissionManager().getPermission(perm.getName()).orElse(null);
    }

    public static void registerCorePermissions() {
        Permission parent = registerPermission(new NukkitPermission(ROOT, "Allows using all Nukkit commands and utilities"));

        Permission broadcasts = registerPermission(new NukkitPermission(ROOT + ".broadcast", "Allows the user to receive all broadcast messages"), parent);

        registerPermission(new NukkitPermission(ROOT + ".broadcast.admin", "Allows the user to receive administrative broadcasts", NukkitPermission.DEFAULT_OP), broadcasts);
        registerPermission(new NukkitPermission(ROOT + ".broadcast.user", "Allows the user to receive user broadcasts", NukkitPermission.DEFAULT_TRUE), broadcasts);

        broadcasts.recalculatePermissibles();

        Permission commands = registerPermission(new NukkitPermission(ROOT + ".command", "Allows using all Nukkit commands"), parent);

        Permission whitelist = registerPermission(new NukkitPermission(ROOT + ".command.whitelist", "Allows the user to modify the NukkitServer whitelist", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.whitelist.add", "Allows the user to add a player to the NukkitServer whitelist"), whitelist);
        registerPermission(new NukkitPermission(ROOT + ".command.whitelist.remove", "Allows the user to remove a player to the NukkitServer whitelist"), whitelist);
        registerPermission(new NukkitPermission(ROOT + ".command.whitelist.reload", "Allows the user to reload the NukkitServer whitelist"), whitelist);
        registerPermission(new NukkitPermission(ROOT + ".command.whitelist.enable", "Allows the user to enable the NukkitServer whitelist"), whitelist);
        registerPermission(new NukkitPermission(ROOT + ".command.whitelist.disable", "Allows the user to disable the NukkitServer whitelist"), whitelist);
        registerPermission(new NukkitPermission(ROOT + ".command.whitelist.list", "Allows the user to list all the players on the NukkitServer whitelist"), whitelist);
        whitelist.recalculatePermissibles();

        Permission ban = registerPermission(new NukkitPermission(ROOT + ".command.ban", "Allows the user to ban people", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.ban.player", "Allows the user to ban players"), ban);
        registerPermission(new NukkitPermission(ROOT + ".command.ban.ip", "Allows the user to ban IP addresses"), ban);
        registerPermission(new NukkitPermission(ROOT + ".command.ban.list", "Allows the user to list all the banned ips or players"), ban);
        ban.recalculatePermissibles();

        Permission unban = registerPermission(new NukkitPermission(ROOT + ".command.unban", "Allows the user to unban people", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.unban.player", "Allows the user to unban players"), unban);
        registerPermission(new NukkitPermission(ROOT + ".command.unban.ip", "Allows the user to unban IP addresses"), unban);
        unban.recalculatePermissibles();

        Permission op = registerPermission(new NukkitPermission(ROOT + ".command.op", "Allows the user to change operators", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.op.give", "Allows the user to give a player operator status"), op);
        registerPermission(new NukkitPermission(ROOT + ".command.op.take", "Allows the user to take a players operator status"), op);
        op.recalculatePermissibles();

        Permission save = registerPermission(new NukkitPermission(ROOT + ".command.save", "Allows the user to save the worlds", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.save.enable", "Allows the user to enable automatic saving"), save);
        registerPermission(new NukkitPermission(ROOT + ".command.save.disable", "Allows the user to disable automatic saving"), save);
        registerPermission(new NukkitPermission(ROOT + ".command.save.perform", "Allows the user to perform a manual save"), save);
        save.recalculatePermissibles();

        Permission time = registerPermission(new NukkitPermission(ROOT + ".command.time", "Allows the user to alter the time", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.time.add", "Allows the user to fast-forward time"), time);
        registerPermission(new NukkitPermission(ROOT + ".command.time.set", "Allows the user to change the time"), time);
        registerPermission(new NukkitPermission(ROOT + ".command.time.start", "Allows the user to restart the time"), time);
        registerPermission(new NukkitPermission(ROOT + ".command.time.stop", "Allows the user to stop the time"), time);
        registerPermission(new NukkitPermission(ROOT + ".command.time.query", "Allows the user query the time"), time);
        time.recalculatePermissibles();

        Permission kill = registerPermission(new NukkitPermission(ROOT + ".command.kill", "Allows the user to kill players", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.kill.self", "Allows the user to commit suicide", NukkitPermission.DEFAULT_TRUE), kill);
        registerPermission(new NukkitPermission(ROOT + ".command.kill.other", "Allows the user to kill other players"), kill);
        kill.recalculatePermissibles();

        Permission gamemode = registerPermission(new NukkitPermission(ROOT + ".command.gamemode", "Allows the user to change the gamemode of players", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.gamemode.survival", "Allows the user to change the gamemode to survival", NukkitPermission.DEFAULT_OP), gamemode);
        registerPermission(new NukkitPermission(ROOT + ".command.gamemode.creative", "Allows the user to change the gamemode to creative", NukkitPermission.DEFAULT_OP), gamemode);
        registerPermission(new NukkitPermission(ROOT + ".command.gamemode.adventure", "Allows the user to change the gamemode to adventure", NukkitPermission.DEFAULT_OP), gamemode);
        registerPermission(new NukkitPermission(ROOT + ".command.gamemode.spectator", "Allows the user to change the gamemode to spectator", NukkitPermission.DEFAULT_OP), gamemode);
        registerPermission(new NukkitPermission(ROOT + ".command.gamemode.other", "Allows the user to change the gamemode of other players", NukkitPermission.DEFAULT_OP), gamemode);
        gamemode.recalculatePermissibles();

        registerPermission(new NukkitPermission(ROOT + ".command.me", "Allows the user to perform a chat action", NukkitPermission.DEFAULT_TRUE), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.tell", "Allows the user to privately message another player", NukkitPermission.DEFAULT_TRUE), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.say", "Allows the user to talk as the console", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.give", "Allows the user to give items to players", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.effect", "Allows the user to give/take potion effects", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.particle", "Allows the user to create particle effects", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.teleport", "Allows the user to teleport players", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.kick", "Allows the user to kick players", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.stop", "Allows the user to stop the server", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.list", "Allows the user to list all online players", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.help", "Allows the user to view the help menu", NukkitPermission.DEFAULT_TRUE), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.plugins", "Allows the user to view the list of plugins", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.reload", "Allows the user to reload the NukkitServer settings", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.version", "Allows the user to view the version of the server", NukkitPermission.DEFAULT_TRUE), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.defaultgamemode", "Allows the user to change the default gamemode", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.seed", "Allows the user to view the seed of the world", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.status", "Allows the user to view the NukkitServer performance", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.gc", "Allows the user to fire garbage collection tasks", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.dumpmemory", "Allows the user to dump memory contents", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.timings", "Allows the user to records timings for all plugin events", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.title", "Allows the user to send titles to players", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.spawnpoint", "Allows the user to change player's spawnpoint", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.setworldspawn", "Allows the user to change the world spawn", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.weather", "Allows the user to change the weather", NukkitPermission.DEFAULT_OP), commands);
        registerPermission(new NukkitPermission(ROOT + ".command.xp", "Allows the user to give experience", NukkitPermission.DEFAULT_OP), commands);

        commands.recalculatePermissibles();

        parent.recalculatePermissibles();
    }

}
