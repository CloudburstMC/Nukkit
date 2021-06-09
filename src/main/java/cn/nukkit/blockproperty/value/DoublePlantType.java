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
    @Since("FUTURE")
    SYRINGA("Lilac", false),

    @PowerNukkitOnly
    @Since("FUTURE")
    GRASS("Double Tallgrass", true),

    @PowerNukkitOnly
    @Since("FUTURE")
    FERN("Large Fern", true),

    @PowerNukkitOnly
    @Since("FUTURE")
    ROSE("Rose Bush", false),

    @PowerNukkitOnly
    @Since("FUTURE")
    PAEONIA("Peony", false)
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
