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
public enum SandStoneType {
    @PowerNukkitOnly
    @Since("FUTURE")
    DEFAULT("Sandstone"),

    @PowerNukkitOnly
    @Since("FUTURE")
    HEIROGLYPHS("Chiseled Sandstone"),

    @PowerNukkitOnly
    @Since("FUTURE")
    CUT("Cut Sandstone"),

    @PowerNukkitOnly
    @Since("FUTURE")
    SMOOTH("Smooth Sandstone");

    private final String englishName;

    @PowerNukkitOnly
    @Since("FUTURE")
    public String getEnglishName() {
        return englishName;
    }
}
