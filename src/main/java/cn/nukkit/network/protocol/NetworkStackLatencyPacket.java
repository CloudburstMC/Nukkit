package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class NetworkStackLatencyPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.NETWORK_STACK_LATENCY_PACKET;

    public long timestamp;
    public boolean needResponse;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        timestamp = this.getLLong();
        needResponse = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLLong(timestamp);
        this.putBoolean(needResponse);
    }
}
