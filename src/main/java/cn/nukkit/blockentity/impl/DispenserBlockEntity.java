package cn.nukkit.blockentity.impl;

import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Dispenser;
import cn.nukkit.inventory.DispenserInventory;
import cn.nukkit.inventory.Inventory;
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

public class DispenserBlockEntity extends BaseBlockEntity implements Dispenser {

    private final DispenserInventory inventory = new DispenserInventory(this);

    public DispenserBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForList("Items", CompoundTag.class, tags -> {
            for (CompoundTag itemTag : tags) {
                Item item = ItemUtils.deserializeItem(itemTag);
                this.getInventory().setItem(itemTag.getByte("Slot"), item);
            }
        });
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        List<CompoundTag> items = new ArrayList<>();
        for (Map.Entry<Integer, Item> entry : this.getInventory().getContents().entrySet()) {
            items.add(ItemUtils.serializeItem(entry.getValue(), entry.getKey()));
        }
        tag.listTag("Items", CompoundTag.class, items);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public boolean isValid() {
        return false;
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
    public boolean isSpawnable() {
        return true;
    }
}
