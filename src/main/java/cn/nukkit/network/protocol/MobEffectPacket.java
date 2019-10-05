package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class MobEffectPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.MOB_EFFECT_PACKET;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public static final byte EVENT_ADD = 1;
    public static final byte EVENT_MODIFY = 2;
    public static final byte EVENT_REMOVE = 3;

    public long eid;
    public int eventId;
    public int effectId;
    public int amplifier = 0;
    public boolean particles = true;
    public int duration = 0;

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityRuntimeId(buffer, this.eid);
        buffer.writeByte((byte) this.eventId);
        Binary.writeVarInt(buffer, this.effectId);
        Binary.writeVarInt(buffer, this.amplifier);
        buffer.writeBoolean(this.particles);
        Binary.writeVarInt(buffer, this.duration);
    }
}
