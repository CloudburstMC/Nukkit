package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.Barrel;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.inventory.BarrelInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemUtils;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class BarrelBlockEntity extends BaseBlockEntity implements Barrel {

    private final BarrelInventory inventory = new BarrelInventory(this);

    public BarrelBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForList("Items", CompoundTag.class, tags -> {
            for (CompoundTag itemTag : tags) {
                Item item = ItemUtils.deserializeItem(itemTag);
                this.inventory.setItem(itemTag.getByte("Slot"), item);
            }
        });
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        List<CompoundTag> items = new ArrayList<>();
        for (Map.Entry<Integer, Item> entry : this.inventory.getContents().entrySet()) {
            items.add(ItemUtils.serializeItem(entry.getValue(), entry.getKey()));
        }
        tag.listTag("Items", CompoundTag.class, items);
    }

    @Override
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory());
            }
            super.close();
        }
    }

    @Override
    public void onBreak() {
        for (Item content : this.inventory.getContents().values()) {
            this.getLevel().dropItem(this.getPosition(), content);
        }
        this.inventory.clearAll(); // Stop items from being moved around by another player in the inventory
    }


    @Override
    public boolean isValid() {
        return getBlock().getId() == BlockIds.BARREL;
    }

    @Override
    public BarrelInventory getInventory() {
        return this.inventory;
    }
}