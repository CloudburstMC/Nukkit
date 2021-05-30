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
public enum StoneSlab4Type {
    @PowerNukkitOnly @Since("1.4.0.0-PN") MOSSY_STONE_BRICK(BlockColor.STONE_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") SMOOTH_QUARTZ(BlockColor.QUARTZ_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") STONE(BlockColor.STONE_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") CUT_SANDSTONE(BlockColor.SAND_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") CUT_RED_SANDSTONE(BlockColor.ORANGE_BLOCK_COLOR);
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final ArrayBlockProperty<StoneSlab4Type> PROPERTY = new ArrayBlockProperty<>("stone_slab_type_4", true, values());
    private final BlockColor color;

    private final String englishName;

    StoneSlab4Type(BlockColor color) {
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
