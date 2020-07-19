package cn.nukkit.utils;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@PowerNukkitOnly
@Since("1.3.0.0-PN")
@Getter @Setter
@EqualsAndHashCode(callSuper = true)
@Deprecated
@DeprecationDetails(since = "1.4.0.0-PN", reason = "Moved to a class with more details and unlimited data bits", replaceWith = "InvalidBlockPropertyMetaException")
public class InvalidBlockDamageException extends InvalidBlockPropertyMetaException {
    private final int blockId;
    private final int damage;
    private final int before;

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public InvalidBlockDamageException(int blockId, int damage, int before) {
        super(CommonBlockProperties.LEGACY_PROPERTIES.getBlockProperty(CommonBlockProperties.LEGACY_PROPERTY_NAME),
                before, damage,
                "Invalid block-meta combination. New: "+blockId+":"+damage+", Before: "+blockId+":"+before);
        this.blockId = blockId;
        this.damage = damage;
        this.before = before;
    }
}
