package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import lombok.ToString;

@ToString
public class NetworkSettingsPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.NETWORK_SETTINGS_PACKET;

    public int compressionThreshold;
    public PacketCompressionAlgorithm compressionAlgorithm;
    public boolean clientThrottleEnabled;
    public byte clientThrottleThreshold;
    public float clientThrottleScalar;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {
        this.reset();
        this.putLShort(this.compressionThreshold);
        this.putLShort(this.compressionAlgorithm.ordinal());
        this.putBoolean(this.clientThrottleEnabled);
        this.putByte(this.clientThrottleThreshold);
        this.putLFloat(this.clientThrottleScalar);
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }
}
