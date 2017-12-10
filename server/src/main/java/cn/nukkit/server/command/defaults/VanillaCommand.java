package cn.nukkit.server.command.defaults;

import cn.nukkit.server.command.NukkitCommand;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class VanillaCommand extends NukkitCommand {

    public VanillaCommand(String name) {
        super(name);
    }

    public VanillaCommand(String name, String description) {
        super(name, description);
    }

    public VanillaCommand(String name, String description, String usageMessage) {
        super(name, description, usageMessage);
    }

    public VanillaCommand(String name, String description, String usageMessage, String[] aliases) {
        super(name, description, usageMessage, aliases);
    }
}
