package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;

@PowerNukkitOnly
@Since("1.5.0.0-PN")
public class SyncEntityPropertyPacket extends DataPacket {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final byte NETWORK_ID = ProtocolInfo.SYNC_ENTITY_PROPERTY;

    private CompoundTag data;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        data = getTag();
    }

    @Override
    public void encode() {
        putTag(data);
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public CompoundTag getData() {
        return data;
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public void setData(CompoundTag data) {
        this.data = data;
    }
}
