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
@Since("1.5.0.0-PN")
@AllArgsConstructor
public enum PrismarineBlockType {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    DEFAULT("Prismarine", BlockColor.CYAN_BLOCK_COLOR),

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    DARK("Dark Prismarine", BlockColor.DIAMOND_BLOCK_COLOR),

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    BRICKS("Prismarine Bricks", BlockColor.DIAMOND_BLOCK_COLOR);

    private final String englishName;
    private final BlockColor color;

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public String getEnglishName() {
        return englishName;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public BlockColor getColor() {
        return color;
    }
}
