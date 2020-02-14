package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.ItemFrame;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemUtils;
import cn.nukkit.level.chunk.Chunk;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.Objects;

import static cn.nukkit.block.BlockIds.AIR;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class ItemFrameBlockEntity extends BaseBlockEntity implements ItemFrame {

    private Item item;
    private byte itemRotation;
    private float itemDropChance = 1.0f;

    public ItemFrameBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForCompound("Item", itemTag -> {
            this.item = ItemUtils.deserializeItem(itemTag);
        });
        tag.listenForByte("ItemRotation", value -> this.itemRotation = value);
        tag.listenForFloat("ItemDropChance", value -> this.itemDropChance = value);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        if (this.item != null && !this.item.isNull()) {
            tag.tag(ItemUtils.serializeItem(this.item).toBuilder().build("Item"));
            tag.byteTag("ItemRotation", this.itemRotation);
            tag.floatTag("ItemDropChance", this.itemDropChance);
        }
    }

    @Override
    public boolean isValid() {
        return this.getBlock().getId() == BlockIds.FRAME;
    }

    @Override
    public int getItemRotation() {
        return this.itemRotation;
    }

    @Override
    public void setItemRotation(int itemRotation) {
        if (this.itemRotation != itemRotation) {
            this.itemRotation = (byte) itemRotation;
            this.setDirty();
            this.getLevel().updateComparatorOutputLevel(this.getPosition());
        }
    }

    @Override
    public Item getItem() {
        return this.item.clone();
    }

    @Override
    public void setItem(Item item) {
        if (!Objects.equals(this.item, item)) {
            this.item = item.clone();
            this.setDirty();

            this.getLevel().updateComparatorOutputLevel(this.getPosition());
        }
    }

    @Override
    public float getItemDropChance() {
        return this.itemDropChance;
    }

    @Override
    public void setItemDropChance(float itemDropChance) {
        if (this.itemDropChance != itemDropChance) {
            this.itemDropChance = itemDropChance;
            this.setDirty();
        }
    }

    public void setDirty() {
        this.spawnToAll();
        super.setDirty();
    }

    @Override
    public int getAnalogOutput() {
        return this.getItem() == null || this.getItem().getId() == AIR ? 0 : this.getItemRotation() % 8 + 1;
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}
