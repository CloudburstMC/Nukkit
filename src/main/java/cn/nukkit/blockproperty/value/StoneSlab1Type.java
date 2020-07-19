package cn.nukkit.blockproperty.value;

import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.utils.BlockColor;
import lombok.Getter;

public enum StoneSlab1Type {
    SMOOTH_STONE("Smooth Stone"),
    SANDSTONE(BlockColor.SAND_BLOCK_COLOR),
    WOOD(BlockColor.WOOD_BLOCK_COLOR),
    COBBLESTONE,
    BRICK,
    STONE_BRICK("Stone Brick"),
    QUARTZ(BlockColor.QUARTZ_BLOCK_COLOR),
    NETHER_BRICK(BlockColor.NETHERRACK_BLOCK_COLOR, "Nether Brick");
    public static final ArrayBlockProperty<StoneSlab1Type> PROPERTY = new ArrayBlockProperty<>("stone_slab_type", true, values());
    @Getter
    private final BlockColor color;

    @Getter
    private final String englishName;

    StoneSlab1Type() {
        this(BlockColor.STONE_BLOCK_COLOR);
    }

    StoneSlab1Type(String name) {
        this.color = BlockColor.STONE_BLOCK_COLOR;
        englishName = name;
    }

    StoneSlab1Type(BlockColor color) {
        this.color = color;
        englishName = name().substring(0, 1) + name().substring(1).toLowerCase();
    }
    
    StoneSlab1Type(BlockColor color, String name) {
        this.color = color;
        englishName = name;
    }
}
