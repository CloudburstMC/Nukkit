package com.nukkitx.server.inventory.transaction;

import com.google.common.base.Preconditions;
import com.nukkitx.protocol.bedrock.data.ItemData;

public class InventoryTransactionItemGroup {
    private final ItemData item;
    private int count;
    private boolean overflow;

    public InventoryTransactionItemGroup(ItemData item, int count) {
        this.item = Preconditions.checkNotNull(item, "item");
        this.count = count;
    }

    public boolean add(ItemData item, int count) {
        if (validItem(item)) {
            if ((count <= 0 || this.count <= (Integer.MAX_VALUE - count)) &&
                    (count >= 0 || this.count >= Integer.MIN_VALUE - count)) {
                this.count += count;
            } else {
                this.overflow = true;
            }
            return true;
        }
        return false;
    }

    public int getCount() {
        return count;
    }

    public boolean hasOverflow() {
        return overflow;
    }

    public ItemData getItem() {
        return ItemData.of(item.getId(), item.getDamage(), 1, item.getTag(), item.getCanPlace(), item.getCanBreak());
    }

    public boolean validItem(ItemData item) {
        return this.item.equals(item, false, true, true);
    }
}
