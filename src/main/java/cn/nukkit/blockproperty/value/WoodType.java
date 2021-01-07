package cn.nukkit.blockproperty.value;

import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.utils.BlockColor;
import lombok.Getter;

public enum WoodType {
    OAK(BlockColor.WOOD_BLOCK_COLOR),
    SPRUCE(BlockColor.SPRUCE_BLOCK_COLOR),
    BIRCH(BlockColor.SAND_BLOCK_COLOR),
    JUNGLE(BlockColor.DIRT_BLOCK_COLOR),
    ACACIA(BlockColor.ORANGE_BLOCK_COLOR),
    DARK_OAK(BlockColor.BROWN_BLOCK_COLOR, "Dark Oak");
    
    @Getter
    private final BlockColor color;
    
    @Getter
    private final String englishName;
    
    public static final ArrayBlockProperty<WoodType> PROPERTY = new ArrayBlockProperty<>("wood_type", true, values());

    WoodType(BlockColor color) {
        this.color = color;
        englishName = name().substring(0, 1) + name().substring(1).toLowerCase();
    }
    
    WoodType(BlockColor color, String name) {
        this.color = color;
        englishName = name;
    }
}
