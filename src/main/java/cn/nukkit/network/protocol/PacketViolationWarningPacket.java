package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class PacketViolationWarningPacket extends DataPacket {

    public Type type;
    public Severity severity;
    public int packetId;
    public String message;

    @Override
    public byte pid() {
        return ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET;
    }

    @Override
    public void decode() {
        this.type = Type.values()[this.getVarInt()];
        this.severity = Severity.values()[this.getVarInt()];
        this.packetId = this.getVarInt();
        this.message = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.type.ordinal());
        this.putVarInt(this.severity.ordinal());
        this.putVarInt(this.packetId);
        this.putString(this.message);
    }

    public static enum Type {

        MALFORMED
    }

    public static enum Severity {

        WARNING,
        FINAL_WARNING,
        TERMINATING_CONNECTION
    }
}
