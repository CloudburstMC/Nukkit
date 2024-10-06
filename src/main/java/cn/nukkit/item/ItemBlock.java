package cn.nukkit.item;

import cn.nukkit.Nukkit;
import cn.nukkit.block.Block;
import cn.nukkit.block.custom.container.BlockStorageContainer;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.material.MaterialType;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemBlock extends Item {

    public ItemBlock(Block block) {
        this(block, 0, 1);
    }

    public ItemBlock(Block block, Integer meta) {
        this(block, meta, 1);
    }

    public ItemBlock(Block block, int meta) {
        this(block, meta, 1);
    }

    public ItemBlock(Block block, Integer meta, int count) {
        super(block.getItemId(), meta, count, block.getName());
        this.block = block;
    }

    public ItemBlock(Block block, int meta, int count) {
        super(block.getItemId(), meta, count, block.getName());
        this.block = block;
    }

    public void setDamage(Integer meta) {
        if (meta != null) {
            this.meta = meta & 0xffff;
        } else {
            this.hasMeta = false;
        }

        if (this.block instanceof BlockStorageContainer) {
            ((BlockStorageContainer) this.block).setStorageFromItem(meta == null ? 0 : meta);
        } else {
            this.block.setDamage(meta);
        }
    }

    @Override
    public ItemBlock clone() {
        ItemBlock block = (ItemBlock) super.clone();
        block.block = this.block.clone();
        return block;
    }

    public Block getBlock() {
        return this.block.clone();
    }

    @Override
    public MaterialType getItemType() {
        return this.block.getBlockType();
    }

    @Override
    public int getMaxStackSize() {
        if (this.block.getId() == Block.SHULKER_BOX || this.block.getId() == Block.UNDYED_SHULKER_BOX) {
            return 1;
        }

        return super.getMaxStackSize();
    }

    @Override
    final public String getName() {
        return this.hasCustomName() ? this.getCustomName() : this.block.getName();
    }

    @Override
    final public String toString() {
        String out = "ItemBlock " + this.block.getName() + " (" + this.id + ':' + (!this.hasMeta ? "?" : this.meta) + ")x" + this.count;
        CompoundTag tag;
        if (Nukkit.DEBUG > 1 && (tag = this.getNamedTag()) != null) {
            out += '\n' + tag.toString();
        }
        return out;
    }
}
