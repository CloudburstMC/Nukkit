package com.nukkitx.server.command.vanilla;

import com.nukkitx.api.command.CommandData;
import com.nukkitx.api.command.sender.CommandSender;
import com.nukkitx.api.message.TranslationMessage;
import com.nukkitx.server.command.VanillaCommand;

public class StopCommand extends VanillaCommand {

    public StopCommand() {
        super("stop", CommandData.builder()
                .description("commands.stop.description")
                //.permission("nukkit.command.stop")
                .build());
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        if (args.length != 0) {
            sender.getServer().shutdown(String.join(" ", args));
        } else {
            sender.getServer().shutdown();
        }
        sender.sendMessage(new TranslationMessage("commands.stop.start"));
        return true;
    }
}
