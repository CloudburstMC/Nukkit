package cn.nukkit.blockproperty.value;

import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.utils.BlockColor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum StoneSlab2Type {
    RED_SANDSTONE(BlockColor.ORANGE_BLOCK_COLOR),
    PURPUR(BlockColor.MAGENTA_BLOCK_COLOR),
    PRISMARINE_ROUGH(BlockColor.CYAN_BLOCK_COLOR),
    PRISMARINE_DARK(BlockColor.DIAMOND_BLOCK_COLOR),
    PRISMARINE_BRICK(BlockColor.DIAMOND_BLOCK_COLOR),
    MOSSY_COBBLESTONE(BlockColor.STONE_BLOCK_COLOR),
    SMOOTH_SANDSTONE(BlockColor.SAND_BLOCK_COLOR),
    RED_NETHER_BRICK(BlockColor.NETHERRACK_BLOCK_COLOR);
    public static final ArrayBlockProperty<StoneSlab2Type> PROPERTY = new ArrayBlockProperty<>("stone_slab_type_2", true, values());
    @Getter
    private final BlockColor color;

    @Getter
    private final String englishName;

    StoneSlab2Type(BlockColor color) {
        this.color = color;
        englishName = Arrays.stream(name().split("_")).map(name-> name.substring(0, 1) + name.substring(1).toLowerCase()).collect(Collectors.joining(" "));
    }
}
