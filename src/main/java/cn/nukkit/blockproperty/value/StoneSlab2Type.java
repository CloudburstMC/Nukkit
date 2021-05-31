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
public enum StoneSlab2Type {
    @PowerNukkitOnly @Since("1.4.0.0-PN") RED_SANDSTONE(BlockColor.ORANGE_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") PURPUR(BlockColor.MAGENTA_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") PRISMARINE_ROUGH(BlockColor.CYAN_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") PRISMARINE_DARK(BlockColor.DIAMOND_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") PRISMARINE_BRICK(BlockColor.DIAMOND_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") MOSSY_COBBLESTONE(BlockColor.STONE_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") SMOOTH_SANDSTONE(BlockColor.SAND_BLOCK_COLOR),
    @PowerNukkitOnly @Since("1.4.0.0-PN") RED_NETHER_BRICK(BlockColor.NETHERRACK_BLOCK_COLOR);
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final ArrayBlockProperty<StoneSlab2Type> PROPERTY = new ArrayBlockProperty<>("stone_slab_type_2", true, values());
    private final BlockColor color;

    private final String englishName;

    StoneSlab2Type(BlockColor color) {
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
