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
        return Block.get(this.getId(), this.getDamage());
    }

    @Override
    public void setDamage(int meta) {
        if (BlockRegistry.get().hasMeta(getId(), meta)) {
            super.setDamage(meta);
        } else {
            super.setDamage(0);
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
