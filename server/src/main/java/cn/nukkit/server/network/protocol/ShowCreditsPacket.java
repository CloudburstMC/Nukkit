package cn.nukkit.server.network.protocol;

public class ShowCreditsPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SHOW_CREDITS_PACKET;

    public long eid;
    public Status status;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.eid);
        this.putVarInt(this.status.ordinal());
    }

    public enum Status {
        START,
        END
    }
}
