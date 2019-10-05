package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class SetSpawnPositionPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.SET_SPAWN_POSITION_PACKET;

    public static final int TYPE_PLAYER_SPAWN = 0;
    public static final int TYPE_WORLD_SPAWN = 1;

    public int spawnType;
    public int y;
    public int z;
    public int x;
    public boolean spawnForced = false;

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVarInt(buffer, this.spawnType);
        Binary.writeBlockVector3(buffer, this.x, this.y, this.z);
        buffer.writeBoolean(this.spawnForced);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

}
