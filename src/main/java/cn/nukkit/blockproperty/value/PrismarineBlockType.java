package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BlockColor;
import lombok.AllArgsConstructor;

/**
 * @author LoboMetalurgico
 * @since 09/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
@AllArgsConstructor
public enum PrismarineBlockType {
    @PowerNukkitOnly
    @Since("FUTURE")
    DEFAULT("Prismarine", BlockColor.CYAN_BLOCK_COLOR),

    @PowerNukkitOnly
    @Since("FUTURE")
    DARK("Dark Prismarine", BlockColor.DIAMOND_BLOCK_COLOR),

    @PowerNukkitOnly
    @Since("FUTURE")
    BRICKS("Prismarine Bricks", BlockColor.DIAMOND_BLOCK_COLOR);

    private final String englishName;
    private final BlockColor color;

    @PowerNukkitOnly
    @Since("FUTURE")
    public String getEnglishName() {
        return englishName;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockColor getColor() {
        return color;
    }
}
