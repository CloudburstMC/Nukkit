package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockItem extends Item {

    public BlockItem(Identifier id) {
        super(id);
    }

    public Block getBlock() {
        return Block.get(this.getId(), this.getMeta());
    }

    @Override
    public void setMeta(int meta) {
        if ((meta & 0xffff) == 0xffff || BlockRegistry.get().hasMeta(getId(), meta)) {
            super.setMeta(meta);
        } else {
            super.setMeta(0);
        }
    }

    @Override
    public int getMaxStackSize() {
        //Shulker boxes don't stack!
        Identifier id = this.getId();
        if (id == BlockIds.SHULKER_BOX || id == BlockIds.UNDYED_SHULKER_BOX) {
            return 1;
        }

        return super.getMaxStackSize();
    }
}
