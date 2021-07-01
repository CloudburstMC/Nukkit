package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class PacketViolationWarningPacket extends DataPacket {

    public static final int TYPE_MALFORMED = 0;

    public static final int SEVERITY_WARNING = 0;
    public static final int SEVERITY_FINAL_WARNING = 1;
    public static final int SEVERITY_TERMINATING_CONNECTION = 2;

    public int type;
    public int severity;
    public int packetId;
    public String message;

    @Override
    public byte pid() {
        return ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET;
    }

    @Override
    public void decode() {
        this.type = this.getVarInt();
        this.severity = this.getVarInt();
        this.packetId = this.getVarInt();
        this.message = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.type);
        this.putVarInt(this.severity);
        this.putVarInt(this.packetId);
        this.putString(this.message);
    }
}
