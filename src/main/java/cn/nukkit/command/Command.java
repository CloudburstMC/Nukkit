package cn.nukkit.command;

import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.registry.CommandRegistry;
import cn.nukkit.utils.TextFormat;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;

import java.util.List;

/**
 * Base class for Commands. Plugins should extend {@link PluginCommand PluginCommand<T>} and not this class.
 *
 * @author MagicDroidX
 * @see PluginCommand
 */
public abstract class Command {

    protected final CommandData commandData;

    private final String name;

    public Timing timing;

    public Command(CommandData data) {
        this(data.getRegisteredName(), data);
    }

    public Command(String name, CommandData data) {
        this.name = name.toLowerCase(); // Uppercase letters crash the client?
        this.commandData = data;
        this.timing = Timings.getCommandTiming(this);
    }

    public abstract boolean execute(CommandSender sender, String commandLabel, String[] args);

    public String getName() {
        return name;
    }

    public String getRegisteredName() {
        return this.commandData.getRegisteredName();
    }

    public void setRegisteredName(String name) {
        if (CommandRegistry.get().isClosed()) {
            throw new IllegalStateException("Trying to set registered command name outside of registration period.");
        }
        this.commandData.setRegisteredName(name);
    }

    public List<String> getPermissions() {
        return this.commandData.getPermissions();
    }

    public boolean testPermission(CommandSender target) {
        if (this.testPermissionSilent(target)) {
            return true;
        }

        if (this.commandData.getPermissionMessage().equals("")) {
            target.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.unknown", this.name));
        } else {
            target.sendMessage(this.commandData.getPermissionMessage());
        }

        return false;
    }

    public boolean testPermissionSilent(CommandSender target) {
        if (this.commandData.getPermissions().size() == 0) {
            return true;
        }

        for (String permission : this.commandData.getPermissions()) {
            if (target.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }

    public String getLabel() {
        return getName();
    }

    public String[] getAliases() {
        return this.commandData.getAliases().toArray(new String[0]);
    }

    public String getPermissionMessage() {
        return commandData.getPermissionMessage();
    }

    public String getDescription() {
        return this.commandData.getDescription();
    }

    public String getUsage() {
        return this.commandData.getUsage();
    }

    public List<CommandParameter[]> getCommandParameters() {
        return this.commandData.getOverloads();
    }

    public void removeAlias(String alias) {
        this.commandData.removeAlias(alias);
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Generates the {@link com.nukkitx.protocol.bedrock.data.CommandData CommandData} used
     * in {@link com.nukkitx.protocol.bedrock.packet.AvailableCommandsPacket AvailableCommandsPacket} which
     * sends the Command data to a client. If the player does not have permission to use this Command,
     * <code>null</code> will be returned.
     *
     * @param player player
     * @return CommandData|null
     */
    public com.nukkitx.protocol.bedrock.data.CommandData toNetwork(Player player) {
        if (!this.testPermission(player)) {
            return null;
        }
        return this.commandData.toNetwork();
    }
}
