package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class VideoStreamConnectPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.VIDEO_STREAM_CONNECT_PACKET;

    public static final byte ACTION_OPEN = 0;
    public static final byte ACTION_CLOSE = 1;

    public String address;
    public float screenshotFrequency;
    public byte action;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeString(buffer, address);
        buffer.writeFloatLE(screenshotFrequency);
        buffer.writeByte(action);
    }
}
