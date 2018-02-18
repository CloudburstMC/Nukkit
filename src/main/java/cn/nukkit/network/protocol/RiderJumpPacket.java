package cn.nukkit.network.protocol;

public class RiderJumpPacket extends DataPacket {

    public int unknown;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("RIDER_JUMP_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.unknown = this.getVarInt();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putVarInt(this.unknown);
    }
}
