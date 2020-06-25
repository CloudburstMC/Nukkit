package cn.nukkit.network.protocol;

import cn.nukkit.api.Since;
import lombok.ToString;

@Since("1.2.2.0-PN")
@ToString
public class PacketViolationWarningPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET;

    public PacketViolationType type;
    public PacketViolationSeverity severity;
    public int packetId;
    public String context;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.type = PacketViolationType.values()[this.getVarInt() + 1];
        this.severity = PacketViolationSeverity.values()[this.getVarInt()];
        this.packetId = this.getVarInt();
        this.context = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.type.ordinal() - 1);
        this.putVarInt(this.severity.ordinal());
        this.putVarInt(this.packetId);
        this.putString(this.context);
    }

    @Since("1.2.2.0-PN")
    public enum PacketViolationType {
        UNKNOWN,
        MALFORMED_PACKET
    }

    @Since("1.2.2.0-PN")
    public enum PacketViolationSeverity {
        UNKNOWN,
        WARNING,
        FINAL_WARNING,
        TERMINATING_CONNECTION
    }
}
