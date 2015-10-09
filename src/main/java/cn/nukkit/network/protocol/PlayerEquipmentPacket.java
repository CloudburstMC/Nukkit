package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerEquipmentPacket extends DataPacket {
    public static final byte NETWORK_ID = Info.PLAYER_EQUIPMENT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long eid;
    public short item;
    public short meta;
    public byte slot;
    public byte selectedSlot;

    @Override
    public void decode() {
        this.eid = this.getLong();
        this.item = this.getShort();
        this.meta = this.getShort();
        this.slot = this.getByte();
        this.selectedSlot = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLong(this.eid);
        this.putShort(this.item);
        this.putShort(this.meta);
        this.putByte(this.slot);
        this.putByte(this.selectedSlot);
    }
}
