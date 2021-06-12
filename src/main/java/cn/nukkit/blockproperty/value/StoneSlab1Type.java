package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public enum StoneSlab1Type {
    @PowerNukkitOnly @Since("1.4.0.0-PN") SMOOTH_STONE("Smooth Stone"),
    @PowerNukkitOnly @Since("1.4.0.0-PN") SANDSTONE(BlockColor.SAND_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") WOOD(BlockColor.WOOD_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") COBBLESTONE,
    @PowerNukkitOnly @Since("1.4.0.0-PN") BRICK,
    @PowerNukkitOnly @Since("1.4.0.0-PN") STONE_BRICK("Stone Brick"),
    @PowerNukkitOnly @Since("1.4.0.0-PN") QUARTZ(BlockColor.QUARTZ_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") NETHER_BRICK(BlockColor.NETHERRACK_BLOCK_COLOR, "Nether Brick");
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final ArrayBlockProperty<StoneSlab1Type> PROPERTY = new ArrayBlockProperty<>("stone_slab_type", true, values());
    private final BlockColor color;

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

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public BlockColor getColor() {
        return this.color;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public String getEnglishName() {
        return this.englishName;
    }
}
