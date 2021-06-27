package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class TickSyncPacket extends DataPacket {

    public long requestTimestamp;
    public long responseTimestamp;

    @Override
    public byte pid() {
        return ProtocolInfo.TICK_SYNC_PACKET;
    }

    @Override
    public void decode() {
        this.requestTimestamp = this.getLLong();
        this.responseTimestamp = this.getLLong();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLLong(this.requestTimestamp);
        this.putLLong(this.responseTimestamp);
    }
}
