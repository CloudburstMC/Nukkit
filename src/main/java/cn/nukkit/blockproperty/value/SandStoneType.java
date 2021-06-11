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
public enum SandStoneType {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    DEFAULT("Sandstone"),

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    HEIROGLYPHS("Chiseled Sandstone"),

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    CUT("Cut Sandstone"),

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    SMOOTH("Smooth Sandstone");

    private final String englishName;

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public String getEnglishName() {
        return englishName;
    }
}
