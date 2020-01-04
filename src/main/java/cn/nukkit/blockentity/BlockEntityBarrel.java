package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.BarrelInventory;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityBarrel extends BlockEntityChestBase {

    public BlockEntityBarrel(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        inventory = new BarrelInventory(this);
        super.initBlockEntity();
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.BARREL)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putBoolean("isMovable", this.isMovable())
                .putBoolean("Findable", false);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == BlockID.BARREL;
    }

    @Override
    public BarrelInventory getInventory() {
        return (BarrelInventory) inventory;
    }
}
