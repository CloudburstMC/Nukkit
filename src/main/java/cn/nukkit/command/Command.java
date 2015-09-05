package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.event.TextContainer;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.permission.Permissible;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Command {

    private String name;

    private String nextLabel;

    private String label;

    private List<String> aliases = new ArrayList<>();

    private List<String> activeAliases = new ArrayList<>();

    private CommandMap commandMap = null;

    protected String description = "";

    protected String usageMessage = "";

    private String permission = null;

    private String permissionMessage = null;

    public Command(String name) {
        this(name, "", null, new ArrayList<>());
    }

    public Command(String name, String description) {
        this(name, description, null, new ArrayList<>());
    }

    public Command(String name, String description, String usageMessage) {
        this(name, description, usageMessage, new ArrayList<>());
    }

    public Command(String name, String description, String usageMessage, List<String> aliases) {
        this.name = name;
        this.nextLabel = name;
        this.label = name;
        this.description = description;
        this.usageMessage = usageMessage == null ? "/" + name : usageMessage;
        this.aliases = aliases;
        this.activeAliases = aliases;
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
            target.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
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
        return commandMap == null || commandMap.equals(this.commandMap);
    }

    public boolean isRegistered() {
        return this.commandMap != null;
    }

    public List<String> getAliases() {
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

    public void setAliases(List<String> aliases) {
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

    public static void broadcastCommandMessage(CommandSender source, String message) {
        broadcastCommandMessage(source, message, true);
    }

    public static void broadcastCommandMessage(CommandSender source, String message, boolean sendToSource) {
        Set<Permissible> users = source.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
        TranslationContainer result = new TranslationContainer("chat.type.admin", new String[]{source.getName(), message});
        TranslationContainer colored = new TranslationContainer(TextFormat.GRAY + TextFormat.ITALIC + "%chat.type.admin", new String[]{source.getName(), message});

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
        String coloredStr = TextFormat.GRAY + TextFormat.ITALIC + resultStr;

        m.setText(resultStr);
        TextContainer result = m.clone();
        m.setText(coloredStr);
        TextContainer colored = m.clone();

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
