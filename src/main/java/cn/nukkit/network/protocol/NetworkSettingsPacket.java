package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class NetworkSettingsPacket extends DataPacket {

    public static final int COMPRESS_NOTHING = 0;
    public static final int COMPRESS_EVERYTHING = 1;

    public int compressionThreshold;

    @Override
    public byte pid() {
        return ProtocolInfo.NETWORK_SETTINGS_PACKET;
    }

    @Override
    public void decode() {
        this.compressionThreshold = this.getLShort();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLShort(this.compressionThreshold);
    }
}
