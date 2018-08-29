package cn.nukkit.network.protocol;

public class NetworkStackLatencyPacket extends DataPacket {

    public long timestamp;

    @Override
    public byte pid() {
        return ProtocolInfo.NETWORK_STACK_LATENCY_PACKET;
    }

    @Override
    public void decode() {
        timestamp = this.getLLong();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLLong(timestamp);
    }
}
