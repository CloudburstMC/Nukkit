package cn.nukkit.network.protocol;

import lombok.ToString;

import java.nio.charset.StandardCharsets;

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

        // BinaryStream::getString()
        int contextLength = (int) this.getUnsignedVarInt();
        String context;

        if (contextLength > 1024) {
            context = "Context too long: " + contextLength;
        } else {
            context = new String(this.get(contextLength), StandardCharsets.UTF_8);
        }

        this.context = context;
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }

    public enum PacketViolationType {
        UNKNOWN,
        MALFORMED_PACKET
    }

    public enum PacketViolationSeverity {
        UNKNOWN,
        WARNING,
        FINAL_WARNING,
        TERMINATING_CONNECTION
    }
}
