package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.locale.TextContainer;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.permission.Permissible;
import cn.nukkit.utils.TextFormat;
import com.google.common.collect.ImmutableMap;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.CommandParamData;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@UtilityClass
public class CommandUtils {
    private static final Pattern RELATIVE_PATTERN = Pattern.compile("(~)?(-?[0-9]*\\.?[0-9]*)");

    public static final ImmutableMap<CommandParamType, CommandParamData.Type> NETWORK_TYPES = ImmutableMap.<CommandParamType, CommandParamData.Type>builder()
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

    public static Optional<Vector3f> parseVector3f(String[] args, Vector3f relative) {
        checkNotNull(args, "args");
        if (args.length < 3) {
            return Optional.empty();
        }

        try {
            return Optional.of(Vector3f.from(
                    getPosition(args[0], relative.getX()),
                    getPosition(args[1], relative.getY()),
                    getPosition(args[2], relative.getZ())
            ));
        } catch (IllegalArgumentException e) {
            // ignore
        }
        return Optional.empty();
    }

    public static float getPosition(String pos, float relative) throws IllegalArgumentException {
        Matcher matcher = RELATIVE_PATTERN.matcher(pos);
        checkArgument(matcher.matches(), "Invalid position");
        float position = Float.parseFloat(matcher.group(2));

        if (matcher.group(1) != null) {
            position += relative;
        }
        return position;
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
        String resultStr = "[" + source.getName() + ": " + m.getText() + "]";

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

}
