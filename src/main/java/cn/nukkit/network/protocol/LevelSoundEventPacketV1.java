package cn.nukkit.network.protocol;


import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class LevelSoundEventPacketV1 extends LevelSoundEventPacket {
    public static final short NETWORK_ID = ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V1;

    public int sound;
    public float x;
    public float y;
    public float z;
    public int extraData = -1; //TODO: Check name
    public int pitch = 1; //TODO: Check name
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
        this.pitch = Binary.readVarInt(buffer);
        this.isBabyMob = buffer.readBoolean();
        this.isGlobal = buffer.readBoolean();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeByte((byte) this.sound);
        Binary.writeVector3f(buffer, this.x, this.y, this.z);
        Binary.writeVarInt(buffer, this.extraData);
        Binary.writeVarInt(buffer, this.pitch);
        buffer.writeBoolean(this.isBabyMob);
        buffer.writeBoolean(this.isGlobal);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
