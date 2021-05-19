package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public enum WoodType {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    OAK(BlockColor.WOOD_BLOCK_COLOR),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    SPRUCE(BlockColor.SPRUCE_BLOCK_COLOR),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    BIRCH(BlockColor.SAND_BLOCK_COLOR),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    JUNGLE(BlockColor.DIRT_BLOCK_COLOR),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    ACACIA(BlockColor.ORANGE_BLOCK_COLOR),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    DARK_OAK(BlockColor.BROWN_BLOCK_COLOR, "Dark Oak");
    
    private final BlockColor color;
    private final String englishName;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final ArrayBlockProperty<WoodType> PROPERTY = new ArrayBlockProperty<>("wood_type", true, values());

    WoodType(BlockColor color) {
        this.color = color;
        englishName = name().substring(0, 1) + name().substring(1).toLowerCase();
    }
    
    WoodType(BlockColor color, String name) {
        this.color = color;
        englishName = name;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockColor getColor() {
        return color;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public String getEnglishName() {
        return englishName;
    }
}
