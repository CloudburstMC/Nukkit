package cn.nukkit.network.protocol;

public class DebugInfoPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.DEBUG_INFO_PACKET;

    public long entityId;
    public String data;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityId = this.getLLong();
        this.data = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLLong(this.entityId);
        this.putString(this.data);
    }
}
