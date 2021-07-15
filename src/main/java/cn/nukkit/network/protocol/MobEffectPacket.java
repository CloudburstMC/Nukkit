package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class MobEffectPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MOB_EFFECT_PACKET;

    public static final byte EVENT_ADD = 1;
    public static final byte EVENT_MODIFY = 2;
    public static final byte EVENT_REMOVE = 3;

    public long entityRuntimeId;
    public byte event;
    public int effectId;
    public int amplifier;
    public boolean enableParticles = true;
    public int duration;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.event = (byte) this.getByte();
        this.effectId = this.getVarInt();
        this.amplifier = this.getVarInt();
        this.enableParticles = this.getBoolean();
        this.duration = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putByte(this.event);
        this.putVarInt(this.effectId);
        this.putVarInt(this.amplifier);
        this.putBoolean(this.enableParticles);
        this.putVarInt(this.duration);
    }
}
