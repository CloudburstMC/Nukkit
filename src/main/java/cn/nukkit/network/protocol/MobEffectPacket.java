package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class MobEffectPacket extends DataPacket {

    public static final byte EVENT_ADD = 1;
    public static final byte EVENT_MODIFY = 2;
    public static final byte EVENT_REMOVE = 3;

    public long entityRuntimeId;
    public byte eventId;
    public int effectId;
    public int amplifier = 0;
    public boolean particles = true;
    public int duration = 0;

    @Override
    public byte pid() {
        return ProtocolInfo.MOB_EFFECT_PACKET;
    }

    @Override
    public void decode() {
    	this.entityRuntimeId = this.getEntityRuntimeId();
		this.eventId = this.getByte();
		this.effectId = this.getVarInt();
		this.amplifier = this.getVarInt();
		this.particles = this.getBoolean();
		this.duration = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putByte(this.eventId);
        this.putVarInt(this.effectId);
        this.putVarInt(this.amplifier);
        this.putBoolean(this.particles);
        this.putVarInt(this.duration);
    }
}
