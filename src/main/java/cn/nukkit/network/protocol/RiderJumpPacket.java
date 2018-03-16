package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class RiderJumpPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RIDER_JUMP_PACKET;

    public int unknown;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.unknown = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.unknown);
    }

    @Override
    protected void handle(Player player) {

    }
}
