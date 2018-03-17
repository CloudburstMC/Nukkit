package cn.nukkit.network.protocol;

import cn.nukkit.Player;

/**
 * @author Nukkit Project Team
 */
public class SetSpawnPositionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_SPAWN_POSITION_PACKET;

    public static final int TYPE_PLAYER_SPAWN = 0;
    public static final int TYPE_WORLD_SPAWN = 1;

    public int spawnType;
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
        this.putVarInt(this.spawnType);
        this.putBlockVector3(this.x, this.y, this.z);
        this.putBoolean(this.spawnForced);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
