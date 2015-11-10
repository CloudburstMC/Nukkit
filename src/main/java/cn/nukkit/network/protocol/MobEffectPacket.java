package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MobEffectPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MOB_EFFECT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public static final byte EVENT_ADD = 1;
    public static final byte EVENT_MODIFY = 2;
    public static final byte EVENT_REMOVE = 3;

    public long eid;
    public byte eventId;
    public byte effectId;
    public byte amplifier;
    public boolean particles = true;
    public int duration;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putLong(this.eid);
        this.putByte(eventId);
        this.putByte(this.effectId);
        this.putByte(this.amplifier);
        this.putByte((byte) (this.particles ? 1 : 0));
        this.putInt(this.duration);
    }
}
