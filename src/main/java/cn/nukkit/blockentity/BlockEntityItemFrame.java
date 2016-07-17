package cn.nukkit.blockentity;

import cn.nukkit.block.BlockAir;
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
        if (!nbt.contains("Item")) {
            nbt.putCompound("Item", NBTIO.putItemHelper(new ItemBlock(new BlockAir())));
        }
        if (!nbt.contains("ItemRotation")) {
            nbt.putByte("ItemRotation", 0);
        }
        if (!nbt.contains("ItemDropChance")) {
            nbt.putFloat("ItemDropChance", 1.0f);
        }
    }

    @Override
    public String getName() {
        return "Item Frame";
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Item.ITEM_FRAME;
    }

    public int getItemRotation() {
        return this.namedTag.getByte("ItemRotation");
    }

    public void setItemRotation(int itemRotation) {
        this.namedTag.putByte("ItemRotation", itemRotation);
        this.setChanged();
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
            this.setChanged();
        }
    }

    public float getItemDropChance() {
        return this.namedTag.getFloat("ItemDropChance");
    }

    public void setItemDropChance(float chance) {
        this.namedTag.putFloat("ItemDropChance", chance);
    }

    private void setChanged() {
        this.spawnToAll();
        if (this.chunk != null) {
            this.chunk.setChanged();
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        if (!this.namedTag.contains("Item")) {
            this.setItem(new ItemBlock(new BlockAir()), false);
        }
        CompoundTag NBTItem = namedTag.getCompound("Item").copy();
        NBTItem.setName("Item");
        boolean item = NBTItem.getShort("id") == Item.AIR;
        return new CompoundTag()
                .putString("id", BlockEntity.ITEM_FRAME)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putCompound("Item", item ? NBTIO.putItemHelper(new ItemBlock(new BlockAir())) : NBTItem)
                .putByte("ItemRotation", item ? 0 : this.getItemRotation())
                .putFloat("ItemDropChance", this.getItemDropChance());

    }
}
