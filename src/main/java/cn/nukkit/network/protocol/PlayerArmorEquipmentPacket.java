package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerArmorEquipmentPacket extends DataPacket {
    public static final byte NETWORK_ID = Info.PLAYER_ARMOR_EQUIPMENT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long eid;
    public byte[] slots = new byte[4];

    @Override
    public void decode() {
        this.eid = this.getLong();
        this.slots = new byte[4];
        this.slots[0] = this.getByte();
        this.slots[1] = this.getByte();
        this.slots[2] = this.getByte();
        this.slots[3] = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLong(this.eid);
        this.putByte(this.slots[0]);
        this.putByte(this.slots[1]);
        this.putByte(this.slots[2]);
        this.putByte(this.slots[3]);
    }
}
