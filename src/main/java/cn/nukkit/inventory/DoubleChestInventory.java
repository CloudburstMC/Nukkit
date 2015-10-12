package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.TileEventPacket;
import cn.nukkit.tile.Chest;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DoubleChestInventory extends BaseInventory implements InventoryHolder {
    private ChestInventory left;
    private ChestInventory right;

    public DoubleChestInventory(Chest left, Chest right) {
        super(null, InventoryType.get(InventoryType.DOUBLE_CHEST));
        this.holder = this;

        this.left = left.getRealInventory();
        this.right = right.getRealInventory();

        int i = 0;
        Map<Integer, Item> items = new HashMap<>();
        for (Item item : this.left.getContents().values()) {
            items.put(i++, item);
        }
        for (Item item : this.right.getContents().values()) {
            items.put(i++, item);
        }

        this.setContents(items);
    }

    @Override
    public Inventory getInventory() {
        return this;
    }

    @Override
    public InventoryHolder getHolder() {
        return this.left.getHolder();
    }

    @Override
    public Item getItem(int index) {
        return index < this.left.getSize() ? this.left.getItem(index) : this.right.getItem(index - this.right.getSize());
    }

    @Override
    public boolean setItem(int index, Item item) {
        return index < this.left.getSize() ? this.left.setItem(index, item) : this.right.setItem(index - this.right.getSize(), item);
    }

    @Override
    public boolean clear(int index) {
        return index < this.left.getSize() ? this.left.clear(index) : this.right.clear(index - this.right.getSize());
    }

    @Override
    public Map<Integer, Item> getContents() {
        Map<Integer, Item> contents = new HashMap<>();

        for (int i = 0; i < this.getSize(); ++i) {
            contents.put(i, this.getItem(i));
        }

        return contents;
    }

    @Override
    public void setContents(Map<Integer, Item> items) {
        if (items.size() > this.size) {
            Map<Integer, Item> newItems = new HashMap<>();
            for (int i = 0; i < this.size; i++) {
                newItems.put(i, items.get(i));
            }
            items = newItems;
        }

        for (int i = 0; i < this.size; i++) {
            if (!items.containsKey(i)) {
                if (i < this.left.size) {
                    if (this.left.slots.containsKey(i)) {
                        this.clear(i);
                    }
                } else if (this.right.slots.containsKey(i - this.left.size)) {
                    this.clear(i);
                }
            } else if (!this.setItem(i, items.get(i))) {
                this.clear(i);
            }
        }
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        if (this.getViewers().size() == 1) {
            TileEventPacket pk = new TileEventPacket();
            pk.x = (int) this.right.getHolder().getX();
            pk.y = (int) this.right.getHolder().getY();
            pk.z = (int) this.right.getHolder().getZ();
            pk.case1 = 1;
            pk.case2 = 2;

            Level level = this.right.getHolder().getLevel();
            if (level != null) {
                level.addChunkPacket((int) this.right.getHolder().getX() >> 4, (int) this.right.getHolder().getZ() >> 4, pk.setChannel(Network.CHANNEL_WORLD_EVENTS));
            }
        }
    }

    @Override
    public void onClose(Player who) {
        if (this.getViewers().size() == 1) {
            TileEventPacket pk = new TileEventPacket();
            pk.x = (int) this.right.getHolder().getX();
            pk.y = (int) this.right.getHolder().getY();
            pk.z = (int) this.right.getHolder().getZ();
            pk.case1 = 1;
            pk.case2 = 0;

            Level level = this.right.getHolder().getLevel();
            if (level != null) {
                level.addChunkPacket((int) this.right.getHolder().getX() >> 4, (int) this.right.getHolder().getZ() >> 4, pk.setChannel(Network.CHANNEL_WORLD_EVENTS));
            }
        }

        super.onClose(who);
    }

    public ChestInventory getLeftSide() {
        return this.left;
    }

    public ChestInventory getRightSide() {
        return this.right;
    }
}
