package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.ContainerBlockEntity;
import cn.nukkit.blockentity.Hopper;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.misc.DroppedItem;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.inventory.HopperInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemUtils;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.nukkitx.math.vector.Vector3i.UP;

/**
 * Created by CreeperFace on 8.5.2017.
 */
public class HopperBlockEntity extends BaseBlockEntity implements Hopper {

    protected final HopperInventory inventory = new HopperInventory(this);

    private final AxisAlignedBB pickupArea = new SimpleAxisAlignedBB(this.getPosition(), this.getPosition().add(1, 2, 1));
    private int transferCooldown = 8;

    public HopperBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForList("Items", CompoundTag.class, tags -> {
            for (CompoundTag itemTag : tags) {
                this.inventory.setItem(itemTag.getByte("Slot"), ItemUtils.deserializeItem(itemTag));
            }
        });
        tag.listenForInt("TransferCooldown", this::setTransferCooldown);

        this.scheduleUpdate();
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        List<CompoundTag> items = new ArrayList<>();
        for (Map.Entry<Integer, Item> entry : this.inventory.getContents().entrySet()) {
            items.add(ItemUtils.serializeItem(entry.getValue(), entry.getKey()));
        }
        tag.listTag("Items", CompoundTag.class, items);

        tag.intTag("TransferCooldown", this.transferCooldown);
    }

    @Override
    public boolean isValid() {
        return this.getLevel().getBlockId(this.getPosition()) == BlockIds.HOPPER;
    }

    public boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    public void setTransferCooldown(int transferCooldown) {
        this.transferCooldown = transferCooldown;
    }

    @Override
    public HopperInventory getInventory() {
        return inventory;
    }

    @Override
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        this.transferCooldown--;

        if (!this.isOnTransferCooldown()) {
            BlockEntity blockEntity = this.getLevel().getBlockEntity(this.getPosition().add(UP));

            boolean changed = pushItems();

            if (!changed) {
                if (blockEntity == null) {
                    changed = pickupItems();
                } else {
                    changed = pullItems();
                }
            }

            if (changed) {
                this.setTransferCooldown(8);
                setDirty();
            }
        }


        return true;
    }

    public boolean pullItems() {
        if (this.inventory.isFull()) {
            return false;
        }

        BlockEntity blockEntity = this.getLevel().getBlockEntity(this.getPosition().add(UP));

        if (blockEntity instanceof ContainerBlockEntity) {
            Inventory inv = ((ContainerBlockEntity) blockEntity).getInventory();
            int[] slots = ((ContainerBlockEntity) blockEntity).getHopperPullSlots();

            if (slots == null || slots.length == 0) {
                return false;
            }

            for (int slot : slots) {
                Item item = inv.getItem(slot);

                if (item.isNull()) {
                    continue;
                }

                item = item.clone();
                item.setCount(1);

                if (!this.inventory.canAddItem(item)) {
                    continue;
                }

                InventoryMoveItemEvent ev = new InventoryMoveItemEvent(inv, this.inventory, this, item, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                this.server.getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    return false;
                }

                Item[] items = this.inventory.addItem(item);

                if (items.length <= 0) {
                    inv.decreaseCount(slot);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean pickupItems() {
        if (this.inventory.isFull()) {
            return false;
        }

        boolean pickedUpItem = false;

        for (Entity entity : this.getLevel().getCollidingEntities(this.pickupArea)) {
            if (entity.isClosed() || !(entity instanceof DroppedItem)) {
                continue;
            }

            DroppedItem itemEntity = (DroppedItem) entity;
            Item item = itemEntity.getItem();

            if (item.isNull()) {
                continue;
            }

            int originalCount = item.getCount();

            if (!this.inventory.canAddItem(item)) {
                continue;
            }

            InventoryMoveItemEvent ev = new InventoryMoveItemEvent(null, this.inventory, this, item, InventoryMoveItemEvent.Action.PICKUP);
            this.server.getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                continue;
            }

            Item[] items = this.inventory.addItem(item);

            if (items.length == 0) {
                entity.close();
                pickedUpItem = true;
                continue;
            }

            if (items[0].getCount() != originalCount) {
                pickedUpItem = true;
                item.setCount(items[0].getCount());
            }
        }

        //TODO: check for minecart
        return pickedUpItem;
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

        for (Item content : inventory.getContents().values()) {
            this.getLevel().dropItem(this.getPosition(), content);
        }
    }

    public boolean pushItems() {
        if (this.inventory.isEmpty()) {
            return false;
        }

        BlockFace direction = BlockFace.fromIndex(this.getBlock().getMeta());
        BlockEntity be = this.getLevel().getBlockEntity(direction.getOffset(this.getPosition()));

        if (be instanceof Hopper && direction == BlockFace.DOWN || !(be instanceof InventoryHolder))
            return false;

        if (be instanceof ContainerBlockEntity) {
            Inventory inv = ((ContainerBlockEntity) be).getInventory();

            for (int i = 0; i < this.inventory.getSize(); i++) {
                Item item = this.inventory.getItem(i);

                if (item.isNull()) {
                    continue;
                }

                int[] slots = ((ContainerBlockEntity) be).getHopperPushSlots(direction, item);

                if (slots == null || slots.length == 0) {
                    continue;
                }

                for (int slot : slots) {
                    Item target = inv.getItem(slot);

                    if (!target.isNull() && (!target.equals(item) || target.getCount() >= item.getMaxStackSize())) {
                        continue;
                    }

                    item.setCount(1);

                    InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.inventory, inv, this, item, InventoryMoveItemEvent.Action.SLOT_CHANGE);
                    this.server.getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return false;
                    }

                    if (target.isNull()) {
                        inv.setItem(slot, item);
                    } else {
                        inv.increaseCount(slot);
                    }

                    inventory.decreaseCount(i);
                    return true;
                }
            }
        }

        //TODO: check for minecart
        return false;
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}
