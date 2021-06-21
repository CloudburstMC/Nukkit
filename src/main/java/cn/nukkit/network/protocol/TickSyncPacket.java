package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import lombok.ToString;

/**
 * @author GoodLucky777
 */
@PowerNukkitOnly
@Since("1.5.0.0-PN")
@ToString
public class TickSyncPacket extends DataPacket {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final byte NETWORK_ID = ProtocolInfo.TICK_SYNC_PACKET;
    
    private long requestTimestamp;
    
    private long responseTimestamp;
    
    @Override
    public byte pid() {
        return NETWORK_ID;
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

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public long getRequestTimestamp() {
        return requestTimestamp;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public void setRequestTimestamp(long requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public long getResponseTimestamp() {
        return responseTimestamp;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public void setResponseTimestamp(long responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }
}
