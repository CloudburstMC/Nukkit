package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class PlayerActionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_ACTION_PACKET;

    public long entityRuntimeId;
    public Action action;
    public int x;
    public int y;
    public int z;
    public int face;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.action = Action.values()[this.getVarInt()];
        BlockVector3 position = this.getBlockVector3();
        this.x = position.getX();
        this.y = position.getY();
        this.z = position.getZ();
        this.face = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putVarInt(this.action.ordinal());
        this.putBlockVector3(this.x, this.y, this.z);
        this.putVarInt(this.face);
    }

    public static enum Action {

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
        DIMENSION_CHANGE_REQUEST,
        DIMENSION_CHANGE_ACK, //Sent when spawning in a different dimension to tell the server we spawned
        START_GLIDE,
        STOP_GLIDE,
        BUILD_DENIED,
        CONTINUE_BREAK,
        CHANGE_SKIN,
        SET_ENCHANTMENT_SEED,
        START_SWIMMING,
        STOP_SWIMMING,
        START_SPIN_ATTACK,
        STOP_SPIN_ATTACK,
        INTERACT_BLOCK,
        PREDICT_DESTROY_BLOCK,
        CONTINUE_DESTROY_BLOCK
    }
}
