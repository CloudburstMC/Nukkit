package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandDataVersions;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.permission.Permissible;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import lombok.Getter;

import java.util.Set;
import java.util.function.Predicate;

@Getter
public abstract class BaseCommand {
    public static final DynamicCommandExceptionType UNKNOWN_COMMAND =
            new DynamicCommandExceptionType(name -> new LiteralMessage("Unknown command " + name));

    private CommandData commandData;

    private String name;
    private String description;
    private String usage = null;
    private String permission = null;
    private String permissionMessage = null;

    public BaseCommand(String name, String description) {
        this.name = name;
        this.description = description;
        this.commandData = new CommandData();
    }

    public String getUsage() {
        // TODO: figure out a way to get the usage from brigadier and still allow
        // plugins to modify it. This can be done by calling `CommandDispatcher.getSmartUsage`,
        // but the problem is that it requires a CommandNode. smh
        return "placeholder";
    }

    public boolean testPermission(CommandSource target) throws CommandSyntaxException {
        if (this.testPermissionSilent(target)) {
            return true;
        }

        if (this.permissionMessage == null) {
            throw UNKNOWN_COMMAND.create(name);
        } else if (!this.permissionMessage.equals("")) {
            target.sendMessage(this.permissionMessage.replace("<permission>", this.permission)); // TODO
        }

        return false;
    }

    public boolean testPermissionSilent(CommandSource target) {
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

    public void sendAdminMessage(CommandSource source, String message) {
        sendAdminMessage(source, message, true);
    }

    public void sendAdminMessage(CommandSource source, String message, boolean sendToSource) {
        // TODO: check for sendCommandFeedback gamerule

        Set<Permissible> users = source.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);

        TranslationContainer result = new TranslationContainer("chat.type.admin", source.getName(), message);
        TranslationContainer colored = new TranslationContainer(TextFormat.GRAY + "" + TextFormat.ITALIC + "%chat.type.admin", source.getName(), message);

        if (sendToSource && !(source instanceof ConsoleCommandSource)) {
            source.sendMessage(message);
        }

        for (Permissible user : users) {
            if (user instanceof CommandSender) {
                if (user instanceof ConsoleCommandSource) {
                    ((ConsoleCommandSource) user).sendMessage(result);
                } else if (!user.equals(source)) {
                    ((CommandSender) user).sendMessage(colored);
                }
            }
        }

    }

    public void sendAdminMessage(CommandSource source, TextContainer message) {
        sendAdminMessage(source, message, true);
    }

    public void sendAdminMessage(CommandSource source, TextContainer message, boolean sendToSource) {
        TextContainer m = message.clone();
        String resultStr = "[" + source.getName() + ": " + (!m.getText().equals(source.getServer().getLanguage().get(m.getText())) ? "%" : "") + m.getText() + "]";

        Set<Permissible> users = source.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);

        String coloredStr = TextFormat.GRAY + "" + TextFormat.ITALIC + resultStr;

        m.setText(resultStr);
        TextContainer result = m.clone();
        m.setText(coloredStr);
        TextContainer colored = m.clone();

        if (sendToSource && !(source instanceof ConsoleCommandSource)) {
            source.sendMessage(message);
        }

        for (Permissible user : users) {
            if (user instanceof CommandSender) {
                if (user instanceof ConsoleCommandSource) {
                    ((ConsoleCommandSource) user).sendMessage(result);
                } else if (!user.equals(source)) {
                    ((CommandSender) user).sendMessage(colored);
                }
            }
        }
    }

    /**
     * Generates modified command data for the specified player
     * for AvailableCommandsPacket.
     *
     * @param player player
     * @return CommandData|null
     */
    public CommandDataVersions generateCustomCommandData(Player player) throws CommandSyntaxException {
        if (!testPermission(player)) {
            return null;
        }

        CommandData customData = this.commandData.clone();
        customData.description = player.getServer().getLanguage().translateString(description);

//            this.commandParameters.forEach((key, par) -> {
//                CommandOverload overload = new CommandOverload();
//                overload.input.parameters = par;
//                customData.overloads.put(key, overload);
//            });
//            if (customData.overloads.size() == 0) customData.overloads.put("default", new CommandOverload());
//            CommandDataVersions versions = new CommandDataVersions();
//            versions.versions.add(customData);
//
        CommandDataVersions versions = new CommandDataVersions();
        versions.versions.add(customData);
        return versions;
    }

    /**
     * Constructs a literal argument.
     *
     * This an an argument where the user must type the literal word provided.
     *
     * For example, in the command "/effect Steve clear", 'effect' and 'clear' are literal
     * arguments, and 'Steve' is normal argument (in this case its a player name).
     */
    protected static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Constructs a required argument.
     */
    protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected final Predicate<CommandSource> requireCheatsEnabled() {
        return source -> {
            Level level = (source instanceof Player) ? ((Player) source).getLevel() : source.getServer().getDefaultLevel();
            return true; // TODO: check if cheats are enabled
        };
    }

    protected final Predicate<CommandSource> requireOp() {
        return source -> (!(source instanceof Player)) || source.isOp();
    }

    protected final Predicate<CommandSource> requirePermission(String permission) {
        this.permission = permission;
        return this::testPermissionSilent;
    }
}
