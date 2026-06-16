package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import lombok.ToString;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
@ToString
public class MobArmorEquipmentPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    private static final Item AIR = Item.get(Item.AIR);

    public long eid;
    public Item[] slots = new Item[4];
    public Item body = AIR;

    @Override
    public void decode() {
        this.eid = this.getEntityRuntimeId();
        this.slots = new Item[4];
        this.slots[0] = this.getNetworkItemStackDescriptor();
        this.slots[1] = this.getNetworkItemStackDescriptor();
        this.slots[2] = this.getNetworkItemStackDescriptor();
        this.slots[3] = this.getNetworkItemStackDescriptor();
        this.body = this.getNetworkItemStackDescriptor();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.eid);
        this.putNetworkItemStackDescriptor(this.slots[0]);
        this.putNetworkItemStackDescriptor(this.slots[1]);
        this.putNetworkItemStackDescriptor(this.slots[2]);
        this.putNetworkItemStackDescriptor(this.slots[3]);
        this.putNetworkItemStackDescriptor(this.body);
    }
}
