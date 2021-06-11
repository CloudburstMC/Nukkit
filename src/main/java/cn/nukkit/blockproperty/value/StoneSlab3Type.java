package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Collectors;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public enum StoneSlab3Type {
    @PowerNukkitOnly @Since("1.4.0.0-PN") END_STONE_BRICK(BlockColor.SAND_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") SMOOTH_RED_SANDSTONE(BlockColor.ORANGE_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") POLISHED_ANDESITE(BlockColor.STONE_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") ANDESITE(BlockColor.STONE_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") DIORITE(BlockColor.QUARTZ_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") POLISHED_DIORITE(BlockColor.QUARTZ_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") GRANITE(BlockColor.DIRT_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") POLISHED_GRANITE(BlockColor.DIRT_BLOCK_COLOR);
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final ArrayBlockProperty<StoneSlab3Type> PROPERTY = new ArrayBlockProperty<>("stone_slab_type_3", true, values());
    private final BlockColor color;

    private final String englishName;

    StoneSlab3Type(BlockColor color) {
        this.color = color;
        englishName = Arrays.stream(name().split("_")).map(name-> name.substring(0, 1) + name.substring(1).toLowerCase()).collect(Collectors.joining(" "));
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
