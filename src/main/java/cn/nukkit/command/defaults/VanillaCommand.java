package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class VanillaCommand extends Command {
    public VanillaCommand(String name) {
        super(name);
    }

    public VanillaCommand(String name, String description) {
        super(name, description);
    }

    public VanillaCommand(String name, String description, String usageMessage) {
        super(name, description, usageMessage);
    }

    public VanillaCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    public VanillaCommand(String name, String description, String usageMessage, String[] aliases) {
        super(name, description, usageMessage, aliases);
    }
}
