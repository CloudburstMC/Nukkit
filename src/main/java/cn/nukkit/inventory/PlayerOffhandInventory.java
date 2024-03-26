package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.EntityHumanType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class PlayerOffhandInventory extends BaseInventory {

    /**
     * Items that can be put to offhand inventory on Bedrock Edition
     */
    private static final IntSet OFFHAND_ITEMS = new IntOpenHashSet(new int[]{Item.AIR, ItemID.SHIELD, ItemID.ARROW, ItemID.TOTEM, ItemID.MAP, ItemID.FIREWORKS, ItemID.NAUTILUS_SHELL, ItemID.SPARKLER});

    public PlayerOffhandInventory(EntityHumanType holder) {
        super(holder, InventoryType.OFFHAND);
    }

    @Override
    public void setSize(int size) {
        throw new UnsupportedOperationException("Offhand can only carry one item at a time");
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        EntityHuman holder = this.getHolder();
        if (holder instanceof Player && !((Player) holder).spawned) {
            return;
        }

        this.sendContents(this.getViewers());
        this.sendContents(holder.getViewers().values());
    }

    @Override
    public void sendContents(Player... players) {
        Item item = this.getItem(0);

        for (Player player : players) {
            if (player == this.getHolder()) {
                InventoryContentPacket pk = new InventoryContentPacket(); // content vs slot
                pk.inventoryId = ContainerIds.OFFHAND;
                pk.slots = new Item[]{item};
                player.dataPacket(pk);
            } else {
                MobEquipmentPacket pk = this.createMobEquipmentPacket(item);
                player.dataPacket(pk);
            }
        }
    }

    @Override
    public void sendSlot(int index, Player... players) {
        Item item = this.getItem(0);

        for (Player player : players) {
            if (player == this.getHolder()) {
                InventorySlotPacket pk = new InventorySlotPacket(); // slot vs content
                pk.inventoryId = ContainerIds.OFFHAND;
                pk.item = item;
                player.dataPacket(pk);
            } else {
                MobEquipmentPacket pk = this.createMobEquipmentPacket(item);
                player.dataPacket(pk);
            }
        }
    }

    private MobEquipmentPacket createMobEquipmentPacket(Item item) {
        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.eid = this.getHolder().getId();
        pk.item = item;
        pk.inventorySlot = 1;
        pk.windowId = ContainerIds.OFFHAND;
        return pk;
    }

    @Override
    public EntityHuman getHolder() {
        return (EntityHuman) super.getHolder();
    }

    @Override
    public boolean allowedToAdd(int itemId) {
        return OFFHAND_ITEMS.contains(itemId);
    }
}
