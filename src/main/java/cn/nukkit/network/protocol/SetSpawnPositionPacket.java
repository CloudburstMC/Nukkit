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
        reset();
        this.putUnsignedVarInt(unknown);
        this.putBlockCoords(x, y, z);
        this.putBoolean(unknownBool);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
