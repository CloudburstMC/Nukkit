package cn.nukkit.network.protocol;

public class TelemetryEventPacket extends DataPacket {

    public long eid;
    public int unknown1;
    public byte unknown2;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("TELEMETRY_EVENT_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putVarLong(this.eid);
        this.putVarInt(this.unknown1);
        this.putByte(this.unknown2);
    }
}
