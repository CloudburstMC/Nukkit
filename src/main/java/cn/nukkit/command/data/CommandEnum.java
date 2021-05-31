package cn.nukkit.command.data;

import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.MinecraftItemID;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author CreeperFace
 */
public class CommandEnum {
    
    @Since("1.4.0.0-PN")
    public static final CommandEnum ENUM_BOOLEAN = new CommandEnum("Boolean", ImmutableList.of("true", "false"));

    @Since("1.4.0.0-PN")
    public static final CommandEnum ENUM_GAMEMODE = new CommandEnum("GameMode",
            ImmutableList.of("survival", "creative", "s", "c", "adventure", "a", "spectator", "view", "v", "spc"));

    @Since("1.4.0.0-PN")
    public static final CommandEnum ENUM_BLOCK;

    @Since("1.4.0.0-PN")
    public static final CommandEnum ENUM_ITEM;

    static {
        ImmutableList.Builder<String> blocks = ImmutableList.builder();
        for (Field field : BlockID.class.getDeclaredFields()) {
            blocks.add(field.getName().toLowerCase());
        }
        ENUM_BLOCK = new CommandEnum("Block", blocks.build());

        ENUM_ITEM = new CommandEnum("Item", ImmutableList.copyOf(Arrays.stream(MinecraftItemID.values())
            .filter(it -> !it.isTechnical())
            .filter(it -> !it.isEducationEdition())
            .flatMap(it -> Stream.of(Stream.of(it.getNamespacedId())/*, Arrays.stream(it.getAliases())*/).flatMap(Function.identity()))
            .map(it-> it.substring(10).toLowerCase())
            .toArray(String[]::new)
        ));
    }

    private String name;
    private List<String> values;

    @Since("1.4.0.0-PN")
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
