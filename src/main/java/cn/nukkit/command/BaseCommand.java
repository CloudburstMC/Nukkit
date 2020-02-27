package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.permission.Permissible;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.Set;
import java.util.function.Predicate;

public abstract class BaseCommand {
    public static final DynamicCommandExceptionType UNKNOWN_COMMAND =
            new DynamicCommandExceptionType(name -> new LiteralMessage("Unknown command " + name));

    private String name;
    private String permission = null;
    private String permissionMessage = null;

    public BaseCommand(String name, String permission) {
        this.name = name;
        this.permission = permission;
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

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void sendAdminMessage(CommandSource source, String message) {
        sendAdminMessage(source, message, true);
    }

    public void sendAdminMessage(CommandSource source, String message, boolean sendToSource) {
        // TODO: check for sendCommandFeedback gamerule

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

    protected static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected final Predicate<CommandSource> requireCheatsEnabled() {
        return source -> false; // TODO
    }

    protected final Predicate<CommandSource> requireOp() {
        return source -> false; // TODO
    }
}
