package cn.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.command.data.CommandOverload;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.permission.Permissible;
import cn.nukkit.utils.TextFormat;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    protected Map<String, CommandParameter[]> commandParameters = new HashMap<>();

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
        this.name = name;
        this.nextLabel = name;
        this.label = name;
        this.description = description;
        this.usageMessage = usageMessage == null ? "/" + name : usageMessage;
        this.aliases = aliases;
        this.activeAliases = aliases;
        this.timing = Timings.getCommandTiming(this);
        this.commandParameters.put("default", new CommandParameter[]{new CommandParameter("args", "rawtext", true)});
    }

    /**
     * Returns an CommandData containing command data
     *
     * @return CommandData
     */
    public CommandData getDefaultCommandData() {
        return this.commandData;
    }

    public CommandParameter[] getCommandParameters(String key) {
        return commandParameters.get(key);
    }

    public Map<String, CommandParameter[]> getCommandParameters() {
        return commandParameters;
    }

    public void setCommandParameters(Map<String, CommandParameter[]> commandParameters) {
        this.commandParameters = commandParameters;
    }

    public void addCommandParameters(String key, CommandParameter[] parameters) {
        this.commandParameters.put(key, parameters);
    }

    /**
     * Generates modified command data for the specified player
     * for AvailableCommandsPacket.
     *
     * @return CommandData|null
     */
    public CommandDataVersions generateCustomCommandData(Player player) {
        if (!this.testPermission(player)) {
            return null;
        }

        CommandData customData = this.commandData.clone();
        customData.aliases = this.getAliases();
        customData.description = player.getServer().getLanguage().translateString(this.getDescription());
        customData.permission = this.getPermission() != null && player.hasPermission(this.getPermission()) ? "any" : "false";
        this.commandParameters.forEach((key, par) -> {
            CommandOverload overload = new CommandOverload();
            overload.input.parameters = par;
            customData.overloads.put(key, overload);
        });
        if (customData.overloads.size() == 0) customData.overloads.put("default", new CommandOverload());
        CommandDataVersions versions = new CommandDataVersions();
        versions.versions.add(customData);
        return versions;
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
            target.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.notFound", this.name));
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

    public static final CommandData generateDefaultData() {
        if (defaultDataTemplate == null) {
            //defaultDataTemplate = new Gson().fromJson(new InputStreamReader(Server.class.getClassLoader().getResourceAsStream("command_default.json")));
        }
        return defaultDataTemplate.clone();
    }

    public static void broadcastCommandMessage(CommandSender source, String message) {
        broadcastCommandMessage(source, message, true);
    }

    public static void broadcastCommandMessage(CommandSender source, String message, boolean sendToSource) {
        Set<Permissible> users = source.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);

        TranslationContainer result = new TranslationContainer("chat.type.admin", new String[]{source.getName(), message});

        TranslationContainer colored = new TranslationContainer(TextFormat.GRAY + "" + TextFormat.ITALIC + "%chat.type.admin", new String[]{source.getName(), message});

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

}
