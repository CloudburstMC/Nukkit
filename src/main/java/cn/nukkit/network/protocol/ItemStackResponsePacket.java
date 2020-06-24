package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.2.2.0-PN")
public class ItemStackResponsePacket extends DataPacket {
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
