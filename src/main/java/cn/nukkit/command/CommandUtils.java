package cn.nukkit.command;

import com.nukkitx.math.vector.Vector3f;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@UtilityClass
public class CommandUtils {
    private static final Pattern RELATIVE_PATTERN = Pattern.compile("(~)?(-?[0-9]*\\.?[0-9]*)");

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
}
