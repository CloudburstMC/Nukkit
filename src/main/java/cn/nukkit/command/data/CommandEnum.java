package cn.nukkit.command.data;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.ItemID;
import cn.nukkit.potion.Effect;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author CreeperFace
 */
public class CommandEnum {

    public static final CommandEnum ENUM_BOOLEAN = new CommandEnum("Boolean", ImmutableList.of("true", "false"));
    public static final CommandEnum ENUM_GAMEMODE = new CommandEnum("GameMode", ImmutableList.of("default", "creative", "spectator", "survival", "adventure", "d", "c", "s", "a"));
    public static final CommandEnum ENUM_DIFFICULTY = new CommandEnum("Difficulty", ImmutableList.of("peaceful", "p", "easy", "e", "normal", "n", "hard", "h"));
    public static final CommandEnum ENUM_BLOCK;
    public static final CommandEnum ENUM_ITEM;
    public static final CommandEnum ENUM_EFFECTS;

    static {
        ImmutableList.Builder<String> blocks = ImmutableList.builder();
        for (Field field : BlockID.class.getDeclaredFields()) {
            blocks.add(field.getName().toLowerCase(Locale.ROOT));
        }
        ENUM_BLOCK = new CommandEnum("Block", blocks.build());

        ImmutableList.Builder<String> items = ImmutableList.builder();
        for (Field field : ItemID.class.getDeclaredFields()) {
            items.add(field.getName().toLowerCase(Locale.ROOT));
        }
        items.addAll(ENUM_BLOCK.getValues());
        ENUM_ITEM = new CommandEnum("Item", items.build());

        ImmutableList.Builder<String> effects = ImmutableList.builder();
        for (Field field : Effect.class.getDeclaredFields()) {
            if (field.getType() == int.class && field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL)) {
                effects.add(field.getName().toLowerCase(Locale.ROOT));
            }
        }
        ENUM_EFFECTS = new CommandEnum("Effect", effects.build());
    }

    private final String name;
    private final List<String> values;

    public CommandEnum(String name, String... values) {
        this(name, Arrays.asList(values));
    }

    public CommandEnum(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public int hashCode() {
        return name.hashCode();
    }
}
