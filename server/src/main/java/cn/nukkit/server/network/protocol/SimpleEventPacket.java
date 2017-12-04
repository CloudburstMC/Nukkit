package cn.nukkit.server.network.protocol;

public class SimpleEventPacket extends DataPacket {

    public short unknown;

    @Override
    public byte pid() {
        return ProtocolInfo.SIMPLE_EVENT_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putShort(this.unknown);
    }
}
