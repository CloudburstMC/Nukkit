package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ItemNetherSprouts extends Item {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemNetherSprouts() {
        this(0, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemNetherSprouts(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemNetherSprouts(Integer meta, int count) {
        super(NETHER_SPROUTS, 0, count, "Nether Sprouts");
        block = Block.get(BlockID.NETHER_SPROUTS_BLOCK);
    }
}
