package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class MovementEffectPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.__INTERNAL__MOVEMENT_EFFECT_PACKET;

    public long eid;
    public MovementEffectType effect;
    public long duration;
    public long tick;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        if (this.effect == null) {
            throw new NullPointerException("MovementEffectType not set!");
        }

        this.reset();
        this.putEntityRuntimeId(this.eid);
        this.putVarInt(this.effect.ordinal());
        this.putUnsignedVarInt(this.duration);
        this.putUnsignedVarInt(this.tick);
    }

    public enum MovementEffectType {
        GLIDE_BOOST,
        DOLPHIN_BOOST,
        GEYSER_BOOST
    }
}
