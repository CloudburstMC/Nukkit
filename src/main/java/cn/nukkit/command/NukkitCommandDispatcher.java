package cn.nukkit.command;

import cn.nukkit.command.defaults.*;
import cn.nukkit.player.Player;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class NukkitCommandDispatcher {
    private final com.mojang.brigadier.CommandDispatcher<CommandSource> dispatcher = new com.mojang.brigadier.CommandDispatcher<>();

    public NukkitCommandDispatcher() {
        // TODO: store in a registry
        new BanCommand(dispatcher);
        new BanListCommand(dispatcher);
//        new BanIpCommand(dispatcher);
        new VersionCommand(dispatcher);
        new EffectCommand(dispatcher);
        new KickCommand(dispatcher);
        new ListCommand(dispatcher);
        new DeopCommand(dispatcher);
        new OpCommand(dispatcher);
        new SaveCommand(dispatcher);
        new SayCommand(dispatcher);
        new SeedCommand(dispatcher);
        new SaveOffCommand(dispatcher);
        new SaveOnCommand(dispatcher);
        new StopCommand(dispatcher);
        new PluginsCommand(dispatcher);
        new MeCommand(dispatcher);

        // TODO: /kill
        // TODO: /defaultgamemode
        // TODO: /difficulty
        // TODO: /enchant
        // TODO: /gamemode
        // TODO: /gamerule
        // TODO: /give
        // TODO: /help
        // TODO: /pardon
        // TODO: /pardon-ip
        // TODO: /particle
        // TODO: /setworldspawn
        // TODO: /spawnpoint
        // TODO: /status
        // TODO: /tp
        // TODO: /tell
        // TODO: /time
        // TODO: /timings
        // TODO: /title
        // TODO: /weather
        // TODO: /whitelist
        // TODO: /xp


        // Debug
        new DebugPasteCommand(dispatcher);
        new GarbageCollectorCommand(dispatcher);
    }

    // TODO: support for command senders
    public boolean dispatch(Player player, String command) throws CommandSyntaxException {
        dispatcher.execute(command, player.getCommandListener());
        return true;
    }
}
