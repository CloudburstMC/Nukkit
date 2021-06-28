package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class PlayerActionPacket extends DataPacket {

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
    public static final int ACTION_CREATIVE_PLAYER_DESTROY_BLOCK = 13;
    public static final int ACTION_DIMENSION_CHANGE_ACK = 14; //Sent when spawning in a different dimension to tell the server we spawned
    public static final int ACTION_START_GLIDE = 15;
    public static final int ACTION_STOP_GLIDE = 16;
    public static final int ACTION_BUILD_DENIED = 17;
    public static final int ACTION_CRACK_BREAK = 18;
    public static final int ACTION_CHANGE_SKIN = 19;
    public static final int ACTION_SET_ENCHANTMENT_SEED = 20;
    public static final int ACTION_START_SWIMMING = 21;
    public static final int ACTION_STOP_SWIMMING = 22;
    public static final int ACTION_START_SPIN_ATTACK = 23;
    public static final int ACTION_STOP_SPIN_ATTACK = 24;
    public static final int ACTION_INTERACT_BLOCK = 25;
    public static final int ACTION_PREDICT_DESTROY_BLOCK = 26;
    public static final int ACTION_CONTINUE_DESTROY_BLOCK = 27;

    public long entityRuntimeId;
    public int action;
    public int x;
    public int y;
    public int z;
    public int face;

    @Override
    public byte pid() {
        return ProtocolInfo.PLAYER_ACTION_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.action = this.getVarInt();
        this.getBlockVector3(this.x, this.y, this.z);
        this.face = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putVarInt(this.action);
        this.putBlockVector3(this.x, this.y, this.z);
        this.putVarInt(this.face);
    }
}
