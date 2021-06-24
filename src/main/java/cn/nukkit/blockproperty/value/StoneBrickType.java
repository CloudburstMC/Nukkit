package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.AllArgsConstructor;

/**
 * @author LoboMetalurgico
 * @since 09/06/2021
 */

@PowerNukkitOnly
@Since("1.5.0.0-PN")
@AllArgsConstructor
public enum StoneBrickType {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    DEFAULT("Stone Bricks"),

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    MOSSY("Mossy Stone Bricks"),

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    CRACKED("Cracked Stone Bricks"),

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    CHISELED("Chiseled Stone Bricks"),

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    SMOOTH("Smooth Stone Bricks");

    private final String englishName;

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public String getEnglishName() {
        return englishName;
    }
}
