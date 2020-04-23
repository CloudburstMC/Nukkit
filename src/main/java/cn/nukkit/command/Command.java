package cn.nukkit.command;

import cn.nukkit.command.data.*;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.registry.CommandRegistry;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.TextFormat;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.nukkitx.protocol.bedrock.data.CommandEnumData;
import com.nukkitx.protocol.bedrock.data.CommandParamData;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Command {

    private Identifier id;

    protected CommandData commandData;

    private final String name;

    private final String label;

    private String[] aliases;

    protected String description;

    protected String usageMessage;

    private String permission = null;

    private String permissionMessage = null;

    private static final ImmutableMap<CommandParamType, CommandParamData.Type> NETWORK_TYPES = ImmutableMap.<CommandParamType, CommandParamData.Type>builder()
            .put(CommandParamType.INT, CommandParamData.Type.INT)
            .put(CommandParamType.FLOAT, CommandParamData.Type.FLOAT)
            .put(CommandParamType.VALUE, CommandParamData.Type.VALUE)
            .put(CommandParamType.WILDCARD_INT, CommandParamData.Type.WILDCARD_INT)
            .put(CommandParamType.OPERATOR, CommandParamData.Type.OPERATOR)
            .put(CommandParamType.TARGET, CommandParamData.Type.TARGET)
            .put(CommandParamType.WILDCARD_TARGET, CommandParamData.Type.WILDCARD_TARGET)
            .put(CommandParamType.FILE_PATH, CommandParamData.Type.FILE_PATH)
            .put(CommandParamType.STRING, CommandParamData.Type.STRING)
            .put(CommandParamType.POSITION, CommandParamData.Type.POSITION)
            .put(CommandParamType.MESSAGE, CommandParamData.Type.MESSAGE)
            .put(CommandParamType.TEXT, CommandParamData.Type.TEXT)
            .put(CommandParamType.JSON, CommandParamData.Type.JSON)
            .put(CommandParamType.COMMAND, CommandParamData.Type.COMMAND)
            .put(CommandParamType.RAWTEXT, CommandParamData.Type.TEXT)

            .build();

    protected List<CommandParameter[]> commandParameters = new ArrayList<>();

    public Timing timing;

    public Command(String name) {
        this(name, "", null, new String[0]);
    }

    public Command(String name, String description) {
        this(name, description, null, new String[0]);
    }

    public Command(String name, String description, String usageMessage) {
        this(name, description, usageMessage, new String[0]);
    }

    public Command(String name, String description, String usageMessage, String[] aliases) {
        this.commandData = new CommandData();
        this.name = name.toLowerCase(); // Uppercase letters crash the client?!?
        this.label = name;
        this.description = description;
        this.usageMessage = usageMessage == null ? "/" + name : usageMessage;
        this.aliases = aliases;
        this.timing = Timings.getCommandTiming(this);
        this.commandParameters.add(new CommandParameter[]{new CommandParameter("args", CommandParamType.RAWTEXT, true)});
    }

    private static CommandParamData toNetwork(CommandParameter commandParameter) {
        return new CommandParamData(commandParameter.name, commandParameter.optional,
                toNetwork(commandParameter.enumData), NETWORK_TYPES.get(commandParameter.type),
                commandParameter.postFix, Collections.emptyList());
    }

    private static CommandEnumData toNetwork(CommandEnum commandEnum) {
        if (commandEnum == null) {
            return null;
        }
        return new CommandEnumData(commandEnum.getName(), commandEnum.getValues().toArray(new String[0]), false);
    }

    public CommandParameter[] getCommandParameters(int key) {
        return commandParameters.get(key);
    }

    public Map<String, CommandOverload> getOverloads() {
        return this.commandData.overloads;
    }

    public abstract boolean execute(CommandSender sender, String commandLabel, String[] args);

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean testPermission(CommandSender target) {
        if (this.testPermissionSilent(target)) {
            return true;
        }

        if (this.permissionMessage == null) {
            target.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.unknown", this.name));
        } else if (!this.permissionMessage.equals("")) {
            target.sendMessage(this.permissionMessage.replace("<permission>", this.permission));
        }

        return false;
    }

    public boolean testPermissionSilent(CommandSender target) {
        if (this.permission == null || this.permission.equals("")) {
            return true;
        }

        String[] permissions = this.permission.split(";");
        for (String permission : permissions) {
            if (target.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }

    public String getLabel() {
        return label;
    }

    public boolean setLabel(String name) {
/*        this.nextLabel = name;
        if (!this.isRegistered()) {
            this.label = name;
            this.timing = Timings.getCommandTiming(this);
            return true;
        }*/
        return false;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usageMessage;
    }

    public void setAliases(String[] aliases) {
        List<String> old = Lists.newArrayList(this.aliases);
        this.aliases = aliases;
        if (CommandRegistry.get().isRegistered(this)) {
          //  CommandRegistry.get().registerAliases(this.getId(), aliases);
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPermissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
    }

    public void setUsage(String usageMessage) {
        this.usageMessage = usageMessage;
    }

    public List<CommandParameter[]> getCommandParameters() {
        return commandParameters;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void setCommandParameters(List<CommandParameter[]> parameters) {
        this.commandParameters = parameters;
    }

    public void addCommandParameters(CommandParameter[] parameters) {
        this.commandParameters.add(parameters);
    }

    /**
     * Generates modified command data for the specified player
     * for AvailableCommandsPacket.
     *
     * @param player player
     * @return CommandData|null
     */
    public com.nukkitx.protocol.bedrock.data.CommandData generateCustomCommandData(Player player) {
        if (!this.testPermission(player)) {
            return null;
        }

        String[] aliasesEnum;
        if (getAliases().length > 0) {
            Set<String> aliasList = new HashSet<>();
            Collections.addAll(aliasList, this.getAliases());
            aliasList.add(this.name.toLowerCase());

            aliasesEnum = aliasList.toArray(new String[0]);
        } else {
            aliasesEnum = new String[]{this.name.toLowerCase()};
        }
        CommandEnumData aliases = new CommandEnumData(this.name.toLowerCase() + "Aliases", aliasesEnum, false);
        String description = player.getServer().getLanguage().translate(this.description);

        CommandParamData[][] overloads = new CommandParamData[this.commandParameters.size()][];

        for (int i = 0; i < overloads.length; i++) {
            CommandParameter[] parameters = this.commandParameters.get(i);
            CommandParamData[] params = new CommandParamData[parameters.length];
            for (int i2 = 0; i2 < parameters.length; i2++) {
                params[i2] = toNetwork(parameters[i2]);
            }
            overloads[i] = params;
        }

        return new com.nukkitx.protocol.bedrock.data.CommandData(this.name.toLowerCase(), description, Collections.emptyList(),
                (byte) 0, aliases, overloads);
    }
}
