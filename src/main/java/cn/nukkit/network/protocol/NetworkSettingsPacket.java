package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class NetworkSettingsPacket extends DataPacket {

	public static final short COMPRESS_NOTHING = 0;
	public static final short COMPRESS_EVERYTHING = 1;

	public short compressionThreshold;

    @Override
    public byte pid() {
        return ProtocolInfo.NETWORK_SETTINGS_PACKET;
    }

    @Override
    public void decode() {
    	this.compressionThreshold = this.getShort();
    }

    @Override
    public void encode() {
    	this.reset();
        this.putShort(this.compressionThreshold);
    }
}
