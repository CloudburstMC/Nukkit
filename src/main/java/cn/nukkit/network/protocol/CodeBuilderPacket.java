package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.2.2.0-PN")
public class CodeBuilderPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CODE_BUILDER_PACKET;

    public String url;
    public boolean opening;

    @Override
    public void encode() {
        putString(url);
        putBoolean(opening);
    }

    @Override
    public void decode() {
        url = getString();
        opening = getBoolean();
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
