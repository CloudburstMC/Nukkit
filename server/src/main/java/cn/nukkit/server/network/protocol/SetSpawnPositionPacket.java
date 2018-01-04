package cn.nukkit.server.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class SetSpawnPositionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_SPAWN_POSITION_PACKET;

    public SpawnType spawnType;
    public int y;
    public int z;
    public int x;
    public boolean spawnForced = false;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.spawnType.ordinal());
        this.putBlockVector3(this.x, this.y, this.z);
        this.putBoolean(this.spawnForced);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public enum SpawnType {
        PLAYER_SPAWN,
        WORLD_SPAWN
    }
}
