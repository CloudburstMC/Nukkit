package cn.nukkit.utils;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import lombok.EqualsAndHashCode;

@PowerNukkitOnly
@Since("1.3.0.0-PN")
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
        super(BlockUnknown.UNKNOWN,
                before, damage,
                "Invalid block-meta combination. New: "+blockId+":"+damage+", Before: "+blockId+":"+before);
        this.blockId = blockId;
        this.damage = damage;
        this.before = before;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public int getBlockId() {
        return this.blockId;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public int getDamage() {
        return this.damage;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public int getBefore() {
        return this.before;
    }
}
