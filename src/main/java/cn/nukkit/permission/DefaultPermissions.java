package cn.nukkit.permission;

import cn.nukkit.Server;

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
        Server.getInstance().getPluginManager().addPermission(perm);

        return Server.getInstance().getPluginManager().getPermission(perm.getName());
    }

    public static void registerCorePermissions() {
        Permission parent = registerPermission(new Permission(ROOT, "Allows using all Nukkit commands and utilities"));

        Permission broadcasts = registerPermission(new Permission(ROOT + ".broadcast", "Allows the user to receive all broadcast messages"), parent);

        registerPermission(new Permission(ROOT + ".broadcast.admin", "Allows the user to receive administrative broadcasts", Permission.DEFAULT_OP), broadcasts);
        registerPermission(new Permission(ROOT + ".broadcast.user", "Allows the user to receive user broadcasts", Permission.DEFAULT_TRUE), broadcasts);

        broadcasts.recalculatePermissibles();

        Permission commands = registerPermission(new Permission(ROOT + ".command", "Allows using all Nukkit commands"), parent);

        Permission whitelist = registerPermission(new Permission(ROOT + ".command.whitelist", "Allows the user to modify the server whitelist", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.whitelist.add", "Allows the user to add a player to the server whitelist"), whitelist);
        registerPermission(new Permission(ROOT + ".command.whitelist.remove", "Allows the user to remove a player to the server whitelist"), whitelist);
        registerPermission(new Permission(ROOT + ".command.whitelist.reload", "Allows the user to reload the server whitelist"), whitelist);
        registerPermission(new Permission(ROOT + ".command.whitelist.enable", "Allows the user to enable the server whitelist"), whitelist);
        registerPermission(new Permission(ROOT + ".command.whitelist.disable", "Allows the user to disable the server whitelist"), whitelist);
        registerPermission(new Permission(ROOT + ".command.whitelist.list", "Allows the user to list all the players on the server whitelist"), whitelist);
        whitelist.recalculatePermissibles();

        Permission ban = registerPermission(new Permission(ROOT + ".command.ban", "Allows the user to ban people", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.ban.player", "Allows the user to ban players"), ban);
        registerPermission(new Permission(ROOT + ".command.ban.ip", "Allows the user to ban IP addresses"), ban);
        registerPermission(new Permission(ROOT + ".command.ban.list", "Allows the user to list all the banned ips or players"), ban);
        ban.recalculatePermissibles();

        Permission unban = registerPermission(new Permission(ROOT + ".command.unban", "Allows the user to unban people", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.unban.player", "Allows the user to unban players"), unban);
        registerPermission(new Permission(ROOT + ".command.unban.ip", "Allows the user to unban IP addresses"), unban);
        unban.recalculatePermissibles();

        Permission op = registerPermission(new Permission(ROOT + ".command.op", "Allows the user to change operators", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.op.give", "Allows the user to give a player operator status"), op);
        registerPermission(new Permission(ROOT + ".command.op.take", "Allows the user to take a players operator status"), op);
        op.recalculatePermissibles();

        Permission save = registerPermission(new Permission(ROOT + ".command.save", "Allows the user to save the worlds", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.save.enable", "Allows the user to enable automatic saving"), save);
        registerPermission(new Permission(ROOT + ".command.save.disable", "Allows the user to disable automatic saving"), save);
        registerPermission(new Permission(ROOT + ".command.save.perform", "Allows the user to perform a manual save"), save);
        save.recalculatePermissibles();

        Permission time = registerPermission(new Permission(ROOT + ".command.time", "Allows the user to alter the time", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.time.add", "Allows the user to fast-forward time"), time);
        registerPermission(new Permission(ROOT + ".command.time.set", "Allows the user to change the time"), time);
        registerPermission(new Permission(ROOT + ".command.time.start", "Allows the user to restart the time"), time);
        registerPermission(new Permission(ROOT + ".command.time.stop", "Allows the user to stop the time"), time);
        registerPermission(new Permission(ROOT + ".command.time.query", "Allows the user query the time"), time);
        time.recalculatePermissibles();

        Permission kill = registerPermission(new Permission(ROOT + ".command.kill", "Allows the user to kill players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.kill.self", "Allows the user to commit suicide", Permission.DEFAULT_TRUE), kill);
        registerPermission(new Permission(ROOT + ".command.kill.other", "Allows the user to kill other players"), kill);
        kill.recalculatePermissibles();

        Permission gamemode = registerPermission(new Permission(ROOT + ".command.gamemode", "Allows the user to change the gamemode of players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.gamemode.survival", "Allows the user to change the gamemode to survival", Permission.DEFAULT_OP), gamemode);
        registerPermission(new Permission(ROOT + ".command.gamemode.creative", "Allows the user to change the gamemode to creative", Permission.DEFAULT_OP), gamemode);
        registerPermission(new Permission(ROOT + ".command.gamemode.adventure", "Allows the user to change the gamemode to adventure", Permission.DEFAULT_OP), gamemode);
        registerPermission(new Permission(ROOT + ".command.gamemode.spectator", "Allows the user to change the gamemode to spectator", Permission.DEFAULT_OP), gamemode);
        registerPermission(new Permission(ROOT + ".command.gamemode.other", "Allows the user to change the gamemode of other players", Permission.DEFAULT_OP), gamemode);
        gamemode.recalculatePermissibles();

        registerPermission(new Permission(ROOT + ".command.me", "Allows the user to perform a chat action", Permission.DEFAULT_TRUE), commands);
        registerPermission(new Permission(ROOT + ".command.tell", "Allows the user to privately message another player", Permission.DEFAULT_TRUE), commands);
        registerPermission(new Permission(ROOT + ".command.say", "Allows the user to talk as the console", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.give", "Allows the user to give items to players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.effect", "Allows the user to give/take potion effects", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.particle", "Allows the user to create particle effects", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.teleport", "Allows the user to teleport players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.kick", "Allows the user to kick players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.stop", "Allows the user to stop the server", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.list", "Allows the user to list all online players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.help", "Allows the user to view the help menu", Permission.DEFAULT_TRUE), commands);
        registerPermission(new Permission(ROOT + ".command.plugins", "Allows the user to view the list of plugins", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.reload", "Allows the user to reload the server settings", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.version", "Allows the user to view the version of the server", Permission.DEFAULT_TRUE), commands);
        registerPermission(new Permission(ROOT + ".command.defaultgamemode", "Allows the user to change the default gamemode", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.seed", "Allows the user to view the seed of the world", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.status", "Allows the user to view the server performance", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.gc", "Allows the user to fire garbage collection tasks", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.dumpmemory", "Allows the user to dump memory contents", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.gamerule", "Sets or queries a game rule value", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.timings", "Allows the user to records timings for all plugin events", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.title", "Allows the user to send titles to players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.spawnpoint", "Allows the user to change player's spawnpoint", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.setworldspawn", "Allows the user to change the world spawn", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.weather", "Allows the user to change the weather", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission(ROOT + ".command.xp", "Allows the user to give experience", Permission.DEFAULT_OP), commands);
        
        registerPermission(new Permission(ROOT + ".textcolor", "Allows the user to write colored text", Permission.DEFAULT_OP), commands);

        commands.recalculatePermissibles();

        parent.recalculatePermissibles();
    }

}
