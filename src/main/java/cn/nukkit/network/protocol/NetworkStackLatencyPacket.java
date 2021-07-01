package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class NetworkStackLatencyPacket extends DataPacket {

    public long timestamp;
    public boolean needResponse;

    @Override
    public byte pid() {
        return ProtocolInfo.NETWORK_STACK_LATENCY_PACKET;
    }

    @Override
    public void decode() {
        this.timestamp = this.getLLong();
		this.needResponse = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLLong(this.timestamp);
        this.putBoolean(this.needResponse);
    }
}
