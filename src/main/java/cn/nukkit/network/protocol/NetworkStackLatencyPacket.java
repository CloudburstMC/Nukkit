package cn.nukkit.network.protocol;

public class NetworkStackLatencyPacket extends DataPacket {

    public long timestamp;
    public boolean unknownBool;

    @Override
    public byte pid() {
        return ProtocolInfo.NETWORK_STACK_LATENCY_PACKET;
    }

    @Override
    public void decode() {
        this.timestamp = this.getLLong();
        this.unknownBool = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLLong(timestamp);
        this.putBoolean(unknownBool);
    }
}
