package cn.nukkit.command.data;

import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.ItemID;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @author CreeperFace
 */
public class CommandEnum {
    
    @Since("1.3.2.0-PN")
    public static final CommandEnum ENUM_BOOLEAN = new CommandEnum("Boolean", ImmutableList.of("true", "false"));

    @Since("1.3.2.0-PN")
    public static final CommandEnum ENUM_GAMEMODE = new CommandEnum("GameMode",
            ImmutableList.of("survival", "creative", "s", "c", "adventure", "a", "spectator", "view", "v", "spc"));

    @Since("1.3.2.0-PN")
    public static final CommandEnum ENUM_BLOCK;

    @Since("1.3.2.0-PN")
    public static final CommandEnum ENUM_ITEM;

    static {
        ImmutableList.Builder<String> blocks = ImmutableList.builder();
        for (Field field : BlockID.class.getDeclaredFields()) {
            blocks.add(field.getName().toLowerCase());
        }
        ENUM_BLOCK = new CommandEnum("Block", blocks.build());

        ImmutableList.Builder<String> items = ImmutableList.builder();
        for (Field field : ItemID.class.getDeclaredFields()) {
            items.add(field.getName().toLowerCase());
        }
        items.addAll(ENUM_BLOCK.getValues());
        ENUM_ITEM = new CommandEnum("Item", items.build());
    }

    private String name;
    private List<String> values;

    @Since("1.3.2.0-PN")
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
