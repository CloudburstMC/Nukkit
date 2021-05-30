package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ItemChain extends Item {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemChain() {
        this(0, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemChain(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemChain(Integer meta, int count) {
        super(CHAIN, meta, count, "Chain");
        this.block = Block.get(BlockID.CHAIN_BLOCK);
    }
    
}
