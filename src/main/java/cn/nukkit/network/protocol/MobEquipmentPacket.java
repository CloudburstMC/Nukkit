package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MobEquipmentPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.MOB_EQUIPMENT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long eid;
    public Item item;
    public int inventorySlot;
    public int hotbarSlot;
    public int windowId;

    @Override
    public void decode() {
        this.eid = this.getEntityRuntimeId(); //EntityRuntimeID
        this.item = this.getSlot();
        this.inventorySlot = this.getByte();
        this.hotbarSlot = this.getByte();
        this.windowId = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.eid); //EntityRuntimeID
        this.putSlot(this.item);
        this.putByte((byte) this.inventorySlot);
        this.putByte((byte) this.hotbarSlot);
        this.putByte((byte) this.windowId);
    }

    @Override
    public void handle(Player player) {
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        Item item = player.inventory.getItem(this.hotbarSlot);

        if (!item.equals(this.item)) {
            player.server.getLogger().debug("Tried to equip " + this.item + " but have " + item + " in target slot");
            player.inventory.sendContents(player);
            return;
        }

        player.inventory.equipItem(this.hotbarSlot);

        player.setDataFlag(Player.DATA_FLAGS, Player.DATA_FLAG_ACTION, false);
    }
}
