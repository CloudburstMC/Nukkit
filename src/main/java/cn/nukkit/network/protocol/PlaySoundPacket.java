package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class PlaySoundPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.PLAY_SOUND_PACKET;

    public String name;
    public int x;
    public int y;
    public int z;
    public float volume;
    public float pitch;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeString(buffer, this.name);
        Binary.writeBlockVector3(buffer, this.x * 8, this.y * 8, this.z * 8);
        buffer.writeFloatLE(this.volume);
        buffer.writeFloatLE(this.pitch);
    }
}
