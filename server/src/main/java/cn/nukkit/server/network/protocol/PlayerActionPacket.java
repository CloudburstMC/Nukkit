package cn.nukkit.server.network.protocol;

import cn.nukkit.server.math.BlockVector3;

/**
 * @author Nukkit Project Team
 */
public class PlayerActionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_ACTION_PACKET;

    public long entityId;
    public Action action;
    public int x;
    public int y;
    public int z;
    public int face;


    @Override
    public void decode() {
        this.entityId = this.getEntityRuntimeId();
        this.action = Action.values()[this.getVarInt()];
        BlockVector3 v = this.getBlockVector3();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.face = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityId);
        this.putVarInt(this.action.ordinal());
        this.putBlockVector3(this.x, this.y, this.z);
        this.putVarInt(this.face);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public enum Action {
        START_BREAK,
        ABORT_BREAK,
        STOP_BREAK,
        GET_UPDATED_BLOCK,
        DROP_ITEM,
        START_SLEEPING,
        STOP_SLEEPING,
        RESPAWN,
        JUMP,
        START_SPRINT,
        STOP_SPRINT,
        START_SNEAK,
        STOP_SNEAK,
        DIMENSION_CHANGE_REQUEST, //sent when dying in different dimension
        DIMENSION_CHANGE_ACK, //sent when spawning in a different dimension to tell the NukkitServer we spawned
        START_GLIDE,
        STOP_GLIDE,
        BUILD_DENIED,
        CONTINUE_BREAK
    }
}
