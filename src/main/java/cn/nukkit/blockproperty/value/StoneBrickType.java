package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.AllArgsConstructor;

/**
 * @author LoboMetalurgico
 * @since 09/06/2021
 */

@PowerNukkitOnly
@Since("FUTURE")
@AllArgsConstructor
public enum StoneBrickType {
    @PowerNukkitOnly
    @Since("FUTURE")
    DEFAULT("Stone Bricks"),

    @PowerNukkitOnly
    @Since("FUTURE")
    MOSSY("Mossy Stone Bricks"),

    @PowerNukkitOnly
    @Since("FUTURE")
    CRACKED("Cracked Stone Bricks"),

    @PowerNukkitOnly
    @Since("FUTURE")
    CHISELED("Chiseled Stone Bricks"),

    @PowerNukkitOnly
    @Since("FUTURE")
    SMOOTH("Smooth Stone Bricks");

    private final String englishName;

    @PowerNukkitOnly
    @Since("FUTURE")
    public String getEnglishName() {
        return englishName;
    }
}
