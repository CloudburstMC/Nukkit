package cn.nukkit.api.event.entity;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

public class EntityInventorySlotChangeEvent implements EntityEvent, Cancellable {
    private final Entity entity;
    private final ItemInstance oldItem;
    private final int slot;
    private ItemInstance newItem;
    private boolean cancelled;

    public EntityInventorySlotChangeEvent(Entity entity, ItemInstance oldItem, ItemInstance newItem, int slot) {
        this.entity = entity;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public ItemInstance getNewItem() {
        return newItem;
    }

    public void setNewItem(ItemInstance newItem) {
        this.newItem = newItem;
    }

    public ItemInstance getOldItem() {
        return oldItem;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
