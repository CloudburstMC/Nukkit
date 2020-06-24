package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.2.2.0-PN")
public class DebugInfoPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.DEBUG_INFO_PACKET;

    public long uniqueEntityId;
    public String data;

    @Override
    public void encode() {
        putVarLong(uniqueEntityId);
        putString(data);
    }

    @Override
    public void decode() {
        uniqueEntityId = getVarLong();
        data = getString();
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
