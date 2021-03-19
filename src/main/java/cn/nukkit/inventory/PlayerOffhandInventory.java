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

import java.util.Arrays;
import java.util.List;

public class PlayerOffhandInventory extends BaseInventory {

    /**
     * Items that can be put to offhand inventory on Bedrock Edition
     */
    private static final List<Integer> OFFHAND_ITEMS = Arrays.asList(ItemID.SHIELD, ItemID.ARROW, ItemID.TOTEM, ItemID.MAP, ItemID.FIREWORKS, ItemID.NAUTILUS_SHELL, 442/*ItemID.SPARKLER*/);

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
        MobEquipmentPacket pk = this.createMobEquipmentPacket(item);

        for (Player player : players) {
            if (player == this.getHolder()) {
                InventoryContentPacket pk2 = new InventoryContentPacket();
                pk2.inventoryId = ContainerIds.OFFHAND;
                pk2.slots = new Item[]{item};
                player.dataPacket(pk2);
            } else {
                player.dataPacket(pk);
            }
        }
    }

    @Override
    public void sendSlot(int index, Player... players) {
        Item item = this.getItem(0);
        MobEquipmentPacket pk = this.createMobEquipmentPacket(item);

        for (Player player : players) {
            if (player == this.getHolder()) {
                InventorySlotPacket pk2 = new InventorySlotPacket();
                pk2.inventoryId = ContainerIds.OFFHAND;
                pk2.item = item;
                player.dataPacket(pk2);
            } else {
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
        pk.tryEncode();
        return pk;
    }

    @Override
    public EntityHuman getHolder() {
        return (EntityHuman) super.getHolder();
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        if (!OFFHAND_ITEMS.contains(item.getId())) return false;
        return super.setItem(index, item, send);
    }
}
