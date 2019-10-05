package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class LevelSoundEventPacketV2 extends LevelSoundEventPacket {
    public static final short NETWORK_ID = ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V2;

    public int sound;
    public float x;
    public float y;
    public float z;
    public int extraData = -1;
    public String entityIdentifier;
    public boolean isBabyMob;
    public boolean isGlobal;

    @Override
    protected void decode(ByteBuf buffer) {
        this.sound = buffer.readByte();
        Vector3f v = Binary.readVector3f(buffer);
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.extraData = Binary.readVarInt(buffer);
        this.entityIdentifier = Binary.readString(buffer);
        this.isBabyMob = buffer.readBoolean();
        this.isGlobal = buffer.readBoolean();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeByte((byte) this.sound);
        Binary.writeVector3f(buffer, this.x, this.y, this.z);
        Binary.writeVarInt(buffer, this.extraData);
        Binary.writeString(buffer, this.entityIdentifier);
        buffer.writeBoolean(this.isBabyMob);
        buffer.writeBoolean(this.isGlobal);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
