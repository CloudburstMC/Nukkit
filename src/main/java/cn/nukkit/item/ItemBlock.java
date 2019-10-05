package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBlock extends Item {

    public ItemBlock(Block block) {
        this(block, 1);
    }

    public ItemBlock(Block block, int count) {
        this(block, block.getDamage(), count);
    }

    public ItemBlock(Block block, int damage, int count) {
        super(block.getItemId(), damage, count, block.getName());
        this.block = block;
    }

    public void setDamage(Integer meta) {
        if (meta != null) {
            this.meta = meta & 0xffff;
        } else {
            this.hasMeta = false;
        }
        this.block.setDamage(meta);
    }

    @Override
    public ItemBlock clone() {
        ItemBlock block = (ItemBlock) super.clone();
        block.block = this.block.clone();
        return block;
    }

    public Block getBlock() {
        return this.block;
    }

    @Override
    public int getMaxStackSize() {
        //Shulker boxes don't stack!
        if (this.getBlock().getId() == Block.SHULKER_BOX || this.getBlock().getId() == Block.UNDYED_SHULKER_BOX) {
            return 1;
        }
        
        return super.getMaxStackSize();
    }

}
