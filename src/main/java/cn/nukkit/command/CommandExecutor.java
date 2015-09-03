package cn.nukkit.command;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}
