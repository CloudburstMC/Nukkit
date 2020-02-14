package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.command.data.*;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.permission.Permissible;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.collect.ImmutableMap;
import com.nukkitx.protocol.bedrock.data.CommandEnumData;
import com.nukkitx.protocol.bedrock.data.CommandParamData;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Command {

    private static CommandData defaultDataTemplate = null;

    protected CommandData commandData;

    private final String name;

    private String nextLabel;

    private String label;

    private String[] aliases = new String[0];

    private String[] activeAliases = new String[0];

    private CommandMap commandMap = null;

    protected String description = "";

    protected String usageMessage = "";

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

    protected List<CommandParameter[]> commandParameters = new ArrayList<>();

    /**
     * Returns an CommandData containing command data
     *
     * @return CommandData
     */
    public CommandData getDefaultCommandData() {
        return this.commandData;
    }

    public Command(String name, String description, String usageMessage, String[] aliases) {
        this.commandData = new CommandData();
        this.name = name.toLowerCase(); // Uppercase letters crash the client?!?
        this.nextLabel = name;
        this.label = name;
        this.description = description;
        this.usageMessage = usageMessage == null ? "/" + name : usageMessage;
        this.aliases = aliases;
        this.activeAliases = aliases;
        this.timing = Timings.getCommandTiming(this);
        this.commandParameters.add(new CommandParameter[]{new CommandParameter("args", CommandParamType.RAWTEXT, true)});
    }

    public static CommandData generateDefaultData() {
        if (defaultDataTemplate == null) {
            //defaultDataTemplate = new Gson().fromJson(new InputStreamReader(Server.class.getClassLoader().getResourceAsStream("command_default.json")));
        }
        return defaultDataTemplate;
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
        this.nextLabel = name;
        if (!this.isRegistered()) {
            this.label = name;
            this.timing = Timings.getCommandTiming(this);
            return true;
        }
        return false;
    }

    public boolean register(CommandMap commandMap) {
        if (this.allowChangesFrom(commandMap)) {
            this.commandMap = commandMap;
            return true;
        }
        return false;
    }

    public boolean unregister(CommandMap commandMap) {
        if (this.allowChangesFrom(commandMap)) {
            this.commandMap = null;
            this.activeAliases = this.aliases;
            this.label = this.nextLabel;
            return true;
        }
        return false;
    }

    public boolean allowChangesFrom(CommandMap commandMap) {
        return commandMap != null && !commandMap.equals(this.commandMap);
    }

    public boolean isRegistered() {
        return this.commandMap != null;
    }

    public String[] getAliases() {
        return this.activeAliases;
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
        this.aliases = aliases;
        if (!this.isRegistered()) {
            this.activeAliases = aliases;
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

    public static void broadcastCommandMessage(CommandSender source, String message) {
        broadcastCommandMessage(source, message, true);
    }

    public static void broadcastCommandMessage(CommandSender source, String message, boolean sendToSource) {
        Set<Permissible> users = source.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);

        TranslationContainer result = new TranslationContainer("chat.type.admin", source.getName(), message);

        TranslationContainer colored = new TranslationContainer(TextFormat.GRAY + "" + TextFormat.ITALIC + "%chat.type.admin", source.getName(), message);

        if (sendToSource && !(source instanceof ConsoleCommandSender)) {
            source.sendMessage(message);
        }

        for (Permissible user : users) {
            if (user instanceof CommandSender) {
                if (user instanceof ConsoleCommandSender) {
                    ((ConsoleCommandSender) user).sendMessage(result);
                } else if (!user.equals(source)) {
                    ((CommandSender) user).sendMessage(colored);
                }
            }
        }
    }

    public static void broadcastCommandMessage(CommandSender source, TextContainer message) {
        broadcastCommandMessage(source, message, true);
    }

    public static void broadcastCommandMessage(CommandSender source, TextContainer message, boolean sendToSource) {
        TextContainer m = message.clone();
        String resultStr = "[" + source.getName() + ": " + (!m.getText().equals(source.getServer().getLanguage().get(m.getText())) ? "%" : "") + m.getText() + "]";

        Set<Permissible> users = source.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);

        String coloredStr = TextFormat.GRAY + "" + TextFormat.ITALIC + resultStr;

        m.setText(resultStr);
        TextContainer result = m.clone();
        m.setText(coloredStr);
        TextContainer colored = m.clone();

        if (sendToSource && !(source instanceof ConsoleCommandSender)) {
            source.sendMessage(message);
        }

        for (Permissible user : users) {
            if (user instanceof CommandSender) {
                if (user instanceof ConsoleCommandSender) {
                    ((ConsoleCommandSender) user).sendMessage(result);
                } else if (!user.equals(source)) {
                    ((CommandSender) user).sendMessage(colored);
                }
            }
        }
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

            aliasesEnum = aliasList.toArray(new String[0]);
        } else {
            aliasesEnum = new String[0];
        }
        CommandEnumData aliases = new CommandEnumData(this.name.toLowerCase() + "-aliases", aliasesEnum, false);

        String description = player.getServer().getLanguage().translateString(this.description);

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
