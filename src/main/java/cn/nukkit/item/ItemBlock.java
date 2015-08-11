package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBlock extends Item {
    public ItemBlock(Block block) {
        this(block, 0, 1);
    }

    public ItemBlock(Block block, int meta) {
        this(block, meta, 1);
    }

    public ItemBlock(Block block, int meta, int count) {
        super(block.getId(), meta, count);
        this.block = block;
    }

    public void setDamage(int meta) {
        this.meta = meta & 0xf;
        this.block.setDamage(meta);
    }

    public void __clone() {
        this.block = this.block.clone();
    }

    public Block getBlock() {
        return this.block;
    }

}
