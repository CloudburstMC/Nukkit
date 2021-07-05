package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class MobArmorEquipmentPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET;

    public long entityRuntimeId;
    public Item[] slots = new Item[4];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        for (int i = 0; i < 4; i++) {
            this.slots[i] = this.getSlot();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        for (Item slot : this.slots) {
            this.putSlot(slot);
        }
    }
}
