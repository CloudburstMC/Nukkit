package cn.nukkit.network.protocol;

public class SimpleEventPacket extends DataPacket {

    public short unknown;

    @Override
    public byte pid() {
        return ProtocolInfo.SIMPLE_EVENT_PACKET;
    }

    @Override
    public void decode() {
        this.unknown = (short) this.getShort();
    }

    @Override
    public void encode() {
        this.reset();
        this.putShort(this.unknown);
    }
}
