package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.5.0.0-PN")
public class RemoveVolumeEntityPacket extends DataPacket {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final byte NETWORK_ID = ProtocolInfo.REMOVE_VOLUME_ENTITY;

    private long id;


    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        id = getUnsignedVarInt();
    }

    @Override
    public void encode() {
        putUnsignedVarInt(id);
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public long getId() {
        return id;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public void setId(long id) {
        this.id = id;
    }
}
