package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class SetSpawnPositionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_SPAWN_POSITION_PACKET;

    public Type type;
    public int x;
    public int y;
    public int z;
    public int dimensionId;
    public int x2 = x;
    public int y2 = y;
    public int z2 = z;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.type = Type.value()[this.getVarInt()];
        BlockVector3 blockVector3 = this.getBlockVector3();
        this.x = blockVector3.getX();
        this.y = blockVector3.getY();
        this.z = blockVector3.getZ();
        this.dimensionId = this.getVarInt();
        blockVector3 = this.getBlockVector3();
        this.x2 = blockVector3.getX();
        this.y2 = blockVector3.getY();
        this.z2 = blockVector3.getZ();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.type.ordinal());
        this.putBlockVector3(this.x, this.y, this.z);
        this.putVarInt(this.dimensionId);
        this.putBlockVector3(this.x2, this.y2, this.z2);
    }

    public static enum Type {

        PLAYER_SPAWN,
        WORLD_SPAWN
    }
}
