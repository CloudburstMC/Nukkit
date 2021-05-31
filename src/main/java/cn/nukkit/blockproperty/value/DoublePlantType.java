package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.RequiredArgsConstructor;

import static io.sentry.util.StringUtils.capitalize;

/**
 * @author joserobjr
 * @since 2021-05-22
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@RequiredArgsConstructor
public enum DoublePlantType {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    SUNFLOWER,

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    LILAC,

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    TALL_GRASS("Double Tallgrass", true),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    LARGE_FERN("Large Fern", true),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    ROSE_BUSH,

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    PEONY
    ;
    private final String englishName;
    private final boolean replaceable;

    @SuppressWarnings("UnstableApiUsage")
    DoublePlantType() {
        englishName = capitalize(name());
        replaceable = false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public String getEnglishName() {
        return englishName;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isReplaceable() {
        return replaceable;
    }
}
