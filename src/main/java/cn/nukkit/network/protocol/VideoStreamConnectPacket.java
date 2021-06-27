package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class VideoStreamConnectPacket extends DataPacket {

    public static final byte ACTION_OPEN = 0;
    public static final byte ACTION_CLOSE = 1;

    public String address;
    public float screenshotFrequency;
    public byte action;

    @Override
    public byte pid() {
        return ProtocolInfo.VIDEO_STREAM_CONNECT_PACKET;
    }

    @Override
    public void decode() {
        this.address = this.getString();
        this.screenshotFrequency = this.getLFloat();
        this.action = this.getByte();
    }

    @Override
    public void encode() {
        this.putString(address);
        this.putLFloat(screenshotFrequency);
        this.putByte(action);
    }
}
