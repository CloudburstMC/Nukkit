package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityEventPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.ENTITY_EVENT_PACKET;


    public static final byte HURT_ANIMATION = 2;
    public static final byte DEATH_ANIMATION = 3;

    public static final byte TAME_FAIL = 6;
    public static final byte TAME_SUCCESS = 7;
    public static final byte SHAKE_WET = 8;
    public static final byte USE_ITEM = 9;
    public static final byte EAT_GRASS_ANIMATION = 10;
    public static final byte FISH_HOOK_BUBBLE = 11;
    public static final byte FISH_HOOK_POSITION = 12;
    public static final byte FISH_HOOK_HOOK = 13;
    public static final byte FISH_HOOK_TEASE = 14;
    public static final byte SQUID_INK_CLOUD = 15;
    public static final byte AMBIENT_SOUND = 16;
    public static final byte RESPAWN = 17;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long eid;
    public byte event;

    @Override
    public void decode() {
        this.eid = this.getLong();
        this.event = (byte) this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLong(this.eid);
        this.putByte(event);
    }
}
