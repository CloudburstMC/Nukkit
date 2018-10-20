package cn.nukkit.network.protocol;

public class EventPacket extends DataPacket {

    public long eid;
    public int unknown1;
    public byte unknown2;

    @Override
    public byte pid() {
        return ProtocolInfo.EVENT_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarLong(this.eid);
        this.putVarInt(this.unknown1);
        this.putByte(this.unknown2);
    }
}
