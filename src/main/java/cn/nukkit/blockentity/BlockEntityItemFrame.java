package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class BlockEntityItemFrame extends BlockEntitySpawnable {

    public BlockEntityItemFrame(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!namedTag.contains("Item")) {
            namedTag.putCompound("Item", NBTIO.putItemHelper(new ItemBlock(Block.get(BlockID.AIR))));
        }
        if (!namedTag.contains("ItemRotation")) {
            namedTag.putByte("ItemRotation", 0);
        }
        if (!namedTag.contains("ItemDropChance")) {
            namedTag.putFloat("ItemDropChance", 1.0f);
        }

        this.level.updateComparatorOutputLevel(this);

        super.initBlockEntity();
    }

    @Override
    public String getName() {
        return "Item Frame";
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Block.ITEM_FRAME_BLOCK;
    }

    public int getItemRotation() {
        return this.namedTag.getByte("ItemRotation");
    }

    public void setItemRotation(int itemRotation) {
        this.namedTag.putByte("ItemRotation", itemRotation);
        this.level.updateComparatorOutputLevel(this);
        this.setDirty();
    }

    public Item getItem() {
        CompoundTag NBTTag = this.namedTag.getCompound("Item");
        return NBTIO.getItemHelper(NBTTag);
    }

    public void setItem(Item item) {
        this.setItem(item, true);
    }

    public void setItem(Item item, boolean setChanged) {
        this.namedTag.putCompound("Item", NBTIO.putItemHelper(item));
        if (setChanged) {
            this.setDirty();
        }

        this.level.updateComparatorOutputLevel(this);
    }

    public float getItemDropChance() {
        return this.namedTag.getFloat("ItemDropChance");
    }

    public void setItemDropChance(float chance) {
        this.namedTag.putFloat("ItemDropChance", chance);
    }

    public void setDirty() {
        this.spawnToAll();
        super.setDirty();
    }

    @Override
    public CompoundTag getSpawnCompound() {
        if (!this.namedTag.contains("Item")) {
            this.setItem(new ItemBlock(Block.get(BlockID.AIR)), false);
        }
        CompoundTag item = namedTag.getCompound("Item").copy();
        item.setName("Item");
        CompoundTag tag = new CompoundTag()
                .putString("id", BlockEntity.ITEM_FRAME)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (item.getShort("id") != Item.AIR) {
            tag.putCompound("Item", item)
                    .putByte("ItemRotation", this.getItemRotation());
        }
        return tag;
    }

    public int getAnalogOutput() {
        return this.getItem() == null || this.getItem().getId() == 0 ? 0 : this.getItemRotation() % 8 + 1;
    }
}
