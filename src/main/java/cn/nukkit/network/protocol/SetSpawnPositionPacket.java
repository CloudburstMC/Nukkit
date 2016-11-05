package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class SetSpawnPositionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_SPAWN_POSITION_PACKET;

    public int unknown;
    public int y;
    public int z;
    public int x;
    public boolean unknownBool;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.unknown);
        this.putBlockCoords(this.x, this.y, this.z);
        this.putBoolean(this.unknownBool);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
