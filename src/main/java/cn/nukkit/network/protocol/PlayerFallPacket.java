package cn.nukkit.network.protocol;

public class PlayerFallPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_FALL_PACKET;

    public float fallDistance;

    @Override
    public void decode() {
        this.fallDistance = this.getLFloat();
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
