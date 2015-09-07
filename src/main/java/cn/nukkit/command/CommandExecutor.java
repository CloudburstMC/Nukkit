package cn.nukkit.command;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface CommandExecutor {

    boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}
