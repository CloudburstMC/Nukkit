package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class PlayerActionPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.PLAYER_ACTION_PACKET;

    public static final int ACTION_START_BREAK = 0;
    public static final int ACTION_ABORT_BREAK = 1;
    public static final int ACTION_STOP_BREAK = 2;
    public static final int ACTION_GET_UPDATED_BLOCK = 3;
    public static final int ACTION_DROP_ITEM = 4;
    public static final int ACTION_START_SLEEPING = 5;
    public static final int ACTION_STOP_SLEEPING = 6;
    public static final int ACTION_RESPAWN = 7;
    public static final int ACTION_JUMP = 8;
    public static final int ACTION_START_SPRINT = 9;
    public static final int ACTION_STOP_SPRINT = 10;
    public static final int ACTION_START_SNEAK = 11;
    public static final int ACTION_STOP_SNEAK = 12;
    public static final int ACTION_DIMENSION_CHANGE_REQUEST = 13; //sent when dying in different dimension
    public static final int ACTION_DIMENSION_CHANGE_ACK = 14; //sent when spawning in a different dimension to tell the server we spawned
    public static final int ACTION_START_GLIDE = 15;
    public static final int ACTION_STOP_GLIDE = 16;
    public static final int ACTION_BUILD_DENIED = 17;
    public static final int ACTION_CONTINUE_BREAK = 18;
    public static final int ACTION_SET_ENCHANTMENT_SEED = 20;
    public static final int ACTION_START_SWIMMING = 21;
    public static final int ACTION_STOP_SWIMMING = 22;
    public static final int ACTION_START_SPIN_ATTACK = 23;
    public static final int ACTION_STOP_SPIN_ATTACK = 24;

    public long entityId;
    public int action;
    public int x;
    public int y;
    public int z;
    public int face;


    @Override
    protected void decode(ByteBuf buffer) {
        this.entityId = Binary.readEntityRuntimeId(buffer);
        this.action = Binary.readVarInt(buffer);
        BlockVector3 v = Binary.readBlockVector3(buffer);
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.face = Binary.readVarInt(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityRuntimeId(buffer, this.entityId);
        Binary.writeVarInt(buffer, this.action);
        Binary.writeBlockVector3(buffer, this.x, this.y, this.z);
        Binary.writeVarInt(buffer, this.face);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

}
