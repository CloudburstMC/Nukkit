package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class NetworkStackLatencyPacket extends DataPacket {

    public long timestamp;
    public boolean unknownBool;

    @Override
    public short pid() {
        return ProtocolInfo.NETWORK_STACK_LATENCY_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        timestamp = buffer.readLongLE();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeLongLE(timestamp);
        buffer.writeBoolean(unknownBool);
    }
}
