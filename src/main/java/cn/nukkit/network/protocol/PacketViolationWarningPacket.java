package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.2.2.0-PN")
public class PacketViolationWarningPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET;
    private static final PacketViolationType[] TYPES = PacketViolationType.values();
    private static final PacketViolationSeverity[] SEVERITIES = PacketViolationSeverity.values();

    public PacketViolationType type;
    public PacketViolationSeverity severity;
    public int packetId;
    public String context;

    @Override
    public void encode() {
        putVarInt(type.ordinal() - 1);
        putVarInt(severity.ordinal());
        putVarInt(packetId);
        putString(context);
    }

    @Override
    public void decode() {
        type = TYPES[getVarInt() + 1];
        severity = SEVERITIES[getVarInt()];
        packetId = getVarInt();
        context = getString();
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @PowerNukkitOnly
    @Since("1.2.2.0-PN")
    public enum PacketViolationType {
        UNKNOWN,
        MALFORMED_PACKET
    }

    @PowerNukkitOnly
    @Since("1.2.2.0-PN")
    public enum PacketViolationSeverity {
        UNKNOWN,
        WARNING,
        FINAL_WARNING,
        TERMINATING_CONNECTION
    }
}
