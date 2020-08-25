package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.ToString;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString
public class ItemStackResponsePacket extends DataPacket {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final byte NETWORK_ID = ProtocolInfo.ITEM_STACK_RESPONSE_PACKET;

    @Override
    public void encode() {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public void decode() {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
