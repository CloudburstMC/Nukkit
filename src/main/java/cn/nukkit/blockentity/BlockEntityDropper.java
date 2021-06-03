package cn.nukkit.blockentity;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.DropperInventory;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@PowerNukkitDifference(info = "Extends BlockEntityEjectable instead of " +
        "BlockEntitySpawnable, BlockEntityContainer, BlockEntityNameable, and InventoryHolder " +
        "only in PowerNukkit", since = "1.4.0.0-PN")
public class BlockEntityDropper extends BlockEntityEjectable {

    protected DropperInventory inventory;

    public BlockEntityDropper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected DropperInventory createInventory() {
        return inventory = new DropperInventory(this);
    }

    @Override
    protected String getBlockEntityName() {
        return BlockEntity.DISPENSER;
    }

    @Override
    public DropperInventory getInventory() {
        return inventory;
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevelBlock().getId() == BlockID.DROPPER;
    }
}
