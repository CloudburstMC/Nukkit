package cn.nukkit.permission;

import cn.nukkit.Server;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public abstract class DefaultPermissions {

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
        Permission parent = registerPermission(new Permission("nukkit", "Allows using all Nukkit commands and utilities"));

        Permission broadcasts = registerPermission(new Permission("nukkit.broadcast", "Allows the user to receive all broadcast messages"), parent);

        registerPermission(new Permission("nukkit.broadcast.admin", "Allows the user to receive administrative broadcasts", Permission.DEFAULT_OP), broadcasts);
        registerPermission(new Permission("nukkit.broadcast.user", "Allows the user to receive user broadcasts", Permission.DEFAULT_TRUE), broadcasts);

        broadcasts.recalculatePermissibles();

        Permission commands = registerPermission(new Permission("nukkit.command", "Allows using all Nukkit commands"), parent);

        Permission whitelist = registerPermission(new Permission("nukkit.command.whitelist", "Allows the user to modify the server whitelist", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.whitelist.add", "Allows the user to add a player to the server whitelist"), whitelist);
        registerPermission(new Permission("nukkit.command.whitelist.remove", "Allows the user to remove a player to the server whitelist"), whitelist);
        registerPermission(new Permission("nukkit.command.whitelist.reload", "Allows the user to reload the server whitelist"), whitelist);
        registerPermission(new Permission("nukkit.command.whitelist.enable", "Allows the user to enable the server whitelist"), whitelist);
        registerPermission(new Permission("nukkit.command.whitelist.disable", "Allows the user to disable the server whitelist"), whitelist);
        registerPermission(new Permission("nukkit.command.whitelist.list", "Allows the user to list all the players on the server whitelist"), whitelist);
        whitelist.recalculatePermissibles();

        Permission ban = registerPermission(new Permission("nukkit.command.ban", "Allows the user to ban people", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.ban.player", "Allows the user to ban players"), ban);
        registerPermission(new Permission("nukkit.command.ban.ip", "Allows the user to ban IP addresses"), ban);
        registerPermission(new Permission("nukkit.command.ban.list", "Allows the user to list all the banned ips or players"), ban);
        ban.recalculatePermissibles();

        Permission unban = registerPermission(new Permission("nukkit.command.unban", "Allows the user to unban people", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.unban.player", "Allows the user to unban players"), unban);
        registerPermission(new Permission("nukkit.command.unban.ip", "Allows the user to unban IP addresses"), unban);
        unban.recalculatePermissibles();

        Permission op = registerPermission(new Permission("nukkit.command.op", "Allows the user to change operators", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.op.give", "Allows the user to give a player operator status"), op);
        registerPermission(new Permission("nukkit.command.op.take", "Allows the user to take a players operator status"), op);
        op.recalculatePermissibles();

        Permission save = registerPermission(new Permission("nukkit.command.save", "Allows the user to save the worlds", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.save.enable", "Allows the user to enable automatic saving"), save);
        registerPermission(new Permission("nukkit.command.save.disable", "Allows the user to disable automatic saving"), save);
        registerPermission(new Permission("nukkit.command.save.perform", "Allows the user to perform a manual save"), save);
        save.recalculatePermissibles();

        Permission time = registerPermission(new Permission("nukkit.command.time", "Allows the user to alter the time", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.time.add", "Allows the user to fast-forward time"), time);
        registerPermission(new Permission("nukkit.command.time.set", "Allows the user to change the time"), time);
        registerPermission(new Permission("nukkit.command.time.start", "Allows the user to restart the time"), time);
        registerPermission(new Permission("nukkit.command.time.stop", "Allows the user to stop the time"), time);
        registerPermission(new Permission("nukkit.command.time.query", "Allows the user query the time"), time);
        time.recalculatePermissibles();

        Permission kill = registerPermission(new Permission("nukkit.command.kill", "Allows the user to kill players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.kill.self", "Allows the user to commit suicide", Permission.DEFAULT_TRUE), kill);
        registerPermission(new Permission("nukkit.command.kill.other", "Allows the user to kill other players"), kill);
        kill.recalculatePermissibles();

        Permission gamemode = registerPermission(new Permission("nukkit.command.gamemode", "Allows the user to change the gamemode of players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.gamemode.survival", "Allows the user to change the gamemode to survival", Permission.DEFAULT_OP), gamemode);
        registerPermission(new Permission("nukkit.command.gamemode.creative", "Allows the user to change the gamemode to creative", Permission.DEFAULT_OP), gamemode);
        registerPermission(new Permission("nukkit.command.gamemode.adventure", "Allows the user to change the gamemode to adventure", Permission.DEFAULT_OP), gamemode);
        registerPermission(new Permission("nukkit.command.gamemode.spectator", "Allows the user to change the gamemode to spectator", Permission.DEFAULT_OP), gamemode);
        registerPermission(new Permission("nukkit.command.gamemode.other", "Allows the user to change the gamemode of other players", Permission.DEFAULT_OP), gamemode);
        gamemode.recalculatePermissibles();

        registerPermission(new Permission("nukkit.command.me", "Allows the user to perform a chat action", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.tell", "Allows the user to privately message another player", Permission.DEFAULT_TRUE), commands);
        registerPermission(new Permission("nukkit.command.say", "Allows the user to talk as the console", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.give", "Allows the user to give items to players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.clear", "Allows the user to clear items from players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.effect", "Allows the user to give/take potion effects", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.particle", "Allows the user to create particle effects", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.teleport", "Allows the user to teleport players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.kick", "Allows the user to kick players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.stop", "Allows the user to stop the server", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.list", "Allows the user to list all online players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.help", "Allows the user to view the help menu", Permission.DEFAULT_TRUE), commands);
        registerPermission(new Permission("nukkit.command.plugins", "Allows the user to view the list of plugins", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.reload", "Allows the user to reload the server settings", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.version", "Allows the user to view the version of the server", Permission.DEFAULT_TRUE), commands);
        registerPermission(new Permission("nukkit.command.version.plugins", "Allows the user to view the version of the plugins", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.defaultgamemode", "Allows the user to change the default gamemode", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.status", "Allows the user to view the server performance", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.gc", "Allows the user to fire garbage collection tasks", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.title", "Allows the user to send titles to players", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.spawnpoint", "Allows the user to change player's spawnpoint", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.setworldspawn", "Allows the user to change the world spawn", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.weather", "Allows the user to change the weather", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.xp", "Allows the user to give experience", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.summon", "Allows the user to summon entities", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.seed", "Allows the user to see world's seed", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.playsound", "Allows the user to play sounds", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.gamerule", "Allows the user to change game rules", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.enchant", "Allows the user to enchant items", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.difficulty", "Allows the user to change difficulty", Permission.DEFAULT_OP), commands);
        registerPermission(new Permission("nukkit.command.world.convert", "Allows the user to convert worlds to LevelDB format", Permission.DEFAULT_FALSE), commands); // Console only

        registerPermission(new Permission("nukkit.textcolor", "Allows the user to write colored text", Permission.DEFAULT_OP), commands);

        commands.recalculatePermissibles();

        parent.recalculatePermissibles();
    }
}
