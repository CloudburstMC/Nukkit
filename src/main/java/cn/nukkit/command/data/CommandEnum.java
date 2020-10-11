package cn.nukkit.command.data;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.ItemID;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author CreeperFace
 */
public class CommandEnum {

    public static final CommandEnum ENUM_BOOLEAN = new CommandEnum("Boolean", ImmutableList.of("true", "false"));
    public static final CommandEnum ENUM_GAMEMODE = new CommandEnum("GameMode",
            ImmutableList.of("survival", "creative", "s", "c", "adventure", "a", "spectator", "view", "v", "spc"));
    public static final CommandEnum ENUM_BLOCK;
    public static final CommandEnum ENUM_ITEM;

    static {
        List<String> blocks = new ArrayList<>();
        for (Field field : BlockID.class.getDeclaredFields()) {
            blocks.add(field.getName().toLowerCase());
        }
        ENUM_BLOCK = new CommandEnum("Block", Collections.unmodifiableList(blocks));

        List<String> items = new ArrayList<>();
        for (Field field : ItemID.class.getDeclaredFields()) {
            items.add(field.getName().toLowerCase());
        }
        items.addAll(blocks);
        ENUM_ITEM = new CommandEnum("Item", Collections.unmodifiableList(items));
    }

    private String name;
    private List<String> values;

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
