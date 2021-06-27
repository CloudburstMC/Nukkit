package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class SetSpawnPositionPacket extends DataPacket {

    public static final int TYPE_PLAYER_SPAWN = 0;
    public static final int TYPE_WORLD_SPAWN = 1;

    public int spawnType;
    public int x;
    public int y;
    public int z;
    public int dimensionId = 0;
    public int x2;
    public int y2;
    public int z2;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_SPAWN_POSITION_PACKET;
    }

    @Override
    public void decode() {
        this.spawnType = this.getVarInt();
        this.getBlockVector3(this.x, this.y, this.z);
        this.dimensionId = this.getVarInt();
        this.getBlockVector3(this.x2, this.y2, this.z2);
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.spawnType);
        this.putBlockVector3(this.x, this.y, this.z);
        this.putVarInt(this.dimensionId);
        this.putBlockVector3(this.x2, this.y2, this.z2);
    }
}
