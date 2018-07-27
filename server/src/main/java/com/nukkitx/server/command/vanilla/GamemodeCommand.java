package com.nukkitx.server.command.vanilla;

import com.nukkitx.api.Player;
import com.nukkitx.api.command.CommandData;
import com.nukkitx.api.command.sender.CommandSender;
import com.nukkitx.api.entity.component.PlayerData;
import com.nukkitx.api.message.TranslationMessage;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.server.command.VanillaCommand;

import java.util.Optional;

public class GamemodeCommand extends VanillaCommand {
    private static CommandData data = CommandData.builder()
            .description("nukkit.command.gamemode.description")
            //.permission("nukkit.command.op.give")
            .usage("commands.generic.usage")
            .build();

    public GamemodeCommand() {
        super("gamemode", data);
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        if (args.length == 0) {
            if (this.getData().getUsage().isPresent()) {
                sender.sendMessage(new TranslationMessage(this.getData().getUsage().get()));
                return false;
            }
        }

        String argument = args[0];
        if (checkGamemode(argument) == null) {
            Optional<Player> optionalPlayer = sender.getServer().getPlayerExact(argument);

            if (!optionalPlayer.isPresent()) {
                return false;
            } else {
                Player player = optionalPlayer.get();
                if (player.get(PlayerData.class).isPresent()) {
                    player.get(PlayerData.class).get().setGamemode(checkGamemode(args[1]));
                    sender.sendMessage(new TranslationMessage("%commands.op.success", player.getName()));
                    player.sendMessage(new TranslationMessage("%commands.op.message"));
                }
            }
        } else {
            Player player = (Player) sender;
            if (player.get(PlayerData.class).isPresent()) {
                player.get(PlayerData.class).get().setGamemode(checkGamemode(args[1]));
                player.sendMessage(new TranslationMessage("%commands.op.success", player.getName()));
                player.sendMessage(new TranslationMessage("%commands.op.message"));
            }
        }
        return true;
    }

    private GameMode checkGamemode(String args) {
        GameMode gameMode = null;
        switch (args) {
            case "creative":
            case "c":
                gameMode = GameMode.CREATIVE;
                break;
            case "survival":
            case "s":
                gameMode = GameMode.SURVIVAL;
                break;
            case "adventure":
            case "a":
                gameMode = GameMode.ADVENTURE;
                break;
        }
        return gameMode;
    }
}
