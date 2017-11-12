package cn.nukkit.network.protocol;

public class ShowCreditsPacket extends DataPacket {

    public static final int STATUS_START_CREDITS = 0;
    public static final int STATUS_END_CREDITS = 1;

    public long eid;
    public int status;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.SHOW_CREDITS_PACKET :
                ProtocolInfo.SHOW_CREDITS_PACKET;
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putEntityRuntimeId(this.eid);
        this.putVarInt(this.status);
    }
}
