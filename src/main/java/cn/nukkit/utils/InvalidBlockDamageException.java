package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@PowerNukkitOnly
@Since("1.3.0.0-PN")
@Getter @Setter
@EqualsAndHashCode(callSuper = true)
public class InvalidBlockDamageException extends IllegalArgumentException {
    private final int blockId;
    private final int damage;
    private final int before;

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public InvalidBlockDamageException(int blockId, int damage, int before) {
        super("Invalid block-meta combination. New: "+blockId+":"+damage+", Before: "+blockId+":"+before);
        this.blockId = blockId;
        this.damage = damage;
        this.before = before;
    }
}
