package com.nukkitx.server.command.vanilla;

import com.nukkitx.api.Player;
import com.nukkitx.api.command.CommandData;
import com.nukkitx.api.command.sender.CommandSender;
import com.nukkitx.api.message.TranslationMessage;
import com.nukkitx.server.command.VanillaCommand;

import java.util.Optional;

public class DeOpCommand extends VanillaCommand {
    private static CommandData data = CommandData.builder()
            .description("nukkit.command.deop.description")
            //.permission("nukkit.command.op.take")
            .usage("commands.generic.usage")
            .build();

    public DeOpCommand() {
        super("deop", data);
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

        String name = args[0];
        Optional<Player> optionalPlayer = sender.getServer().getPlayerExact(name);

        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            player.setOp(false);
            sender.sendMessage(new TranslationMessage("%commands.deop.success", player.getName()));
            player.sendMessage(new TranslationMessage("%commands.deop.message"));
        }
        return true;
    }
}
