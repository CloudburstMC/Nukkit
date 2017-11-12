package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MobArmorEquipmentPacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.MOB_ARMOR_EQUIPMENT_PACKET :
                ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET;
    }

    public long eid;
    public Item[] slots = new Item[4];

    @Override
    public void decode(PlayerProtocol protocol) {
        this.eid = this.getEntityRuntimeId();
        this.slots = new Item[4];
        this.slots[0] = this.getSlot();
        this.slots[1] = this.getSlot();
        this.slots[2] = this.getSlot();
        this.slots[3] = this.getSlot();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putEntityRuntimeId(this.eid);
        this.putSlot(this.slots[0]);
        this.putSlot(this.slots[1]);
        this.putSlot(this.slots[2]);
        this.putSlot(this.slots[3]);
    }
}
