package cn.nukkit.inventory;

import cn.nukkit.blockentity.BlockEntityCampfire;
import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.ArrayList;
import java.util.List;

public class CampfireInventory extends ContainerInventory {

    public CampfireInventory(BlockEntityCampfire campfire) {
        super(campfire, InventoryType.CAMPFIRE);
    }

    @Override
    public BlockEntityCampfire getHolder() {
        return (BlockEntityCampfire) this.holder;
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);

        this.getHolder().scheduleUpdate();
        this.getHolder().spawnToAll();

        this.getHolder().chunk.setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override // Max stack size 1
    public boolean canAddItem(Item item) {
        int count = item.getCount();
        boolean checkDamage = item.hasMeta();
        boolean checkTag = item.getCompoundTag() != null;
        int i1 = this.getSize();
        for (int i = 0; i < i1; ++i) {
            Item slot = this.getItemFast(i);
            if (item.equals(slot, checkDamage, checkTag)) {
                int diff;
                if ((diff = 1 - slot.getCount()) > 0) {
                    count -= diff;
                }
            } else if (slot.getId() == Item.AIR) {
                count -= 1;
            }

            if (count <= 0) {
                return true;
            }
        }

        return false;
    }

    @Override // Max stack size 1
    public Item[] addItem(Item... slots) {
        List<Item> itemSlots = new ArrayList<>();
        for (Item slot : slots) {
            if (slot.getId() != 0 && slot.getCount() > 0) {
                itemSlots.add(slot.clone());
            }
        }

        IntArrayList emptySlots = new IntArrayList();

        for (int i = 0; i < this.getSize(); ++i) {
            Item item = this.getItem(i);
            if (item.getId() == Item.AIR || item.getCount() <= 0) {
                emptySlots.add(i);
            }

            for (Item slot : new ArrayList<>(itemSlots)) {
                if (slot.equals(item) && item.getCount() < 1) {
                    int amount = Math.min(1 - item.getCount(), slot.getCount());
                    amount = Math.min(amount, 1);
                    if (amount > 0) {
                        slot.setCount(slot.getCount() - amount);
                        item.setCount(item.getCount() + amount);
                        this.setItem(i, item);
                        if (slot.getCount() <= 0) {
                            itemSlots.remove(slot);
                        }
                    }
                }
            }
            if (itemSlots.isEmpty()) {
                break;
            }
        }

        if (!itemSlots.isEmpty() && !emptySlots.isEmpty()) {
            for (int slotIndex : emptySlots) {
                if (!itemSlots.isEmpty()) {
                    Item slot = itemSlots.get(0);
                    int amount = Math.min(1, slot.getCount());
                    slot.setCount(slot.getCount() - amount);
                    Item item = slot.clone();
                    item.setCount(amount);
                    this.setItem(slotIndex, item);
                    if (slot.getCount() <= 0) {
                        itemSlots.remove(slot);
                    }
                }
            }
        }

        return itemSlots.toArray(new Item[0]);
    }
}
