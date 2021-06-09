package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import lombok.ToString;

/**
 * @author GoodLucky777
 */
@PowerNukkitOnly
@Since("FUTURE")
@ToString
public class TickSyncPacket extends DataPacket {

    @PowerNukkitOnly
    @Since("FUTURE")
    public static final byte NETWORK_ID = ProtocolInfo.TICK_SYNC_PACKET;
    
    @PowerNukkitOnly
    @Since("FUTURE")
    public long requestTimestamp;
    
    @PowerNukkitOnly
    @Since("FUTURE")
    public long responseTimestamp;
    
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
}
