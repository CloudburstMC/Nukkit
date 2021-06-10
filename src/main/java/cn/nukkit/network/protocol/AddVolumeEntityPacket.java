package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;

@PowerNukkitOnly
@Since("FUTURE")
public class AddVolumeEntityPacket extends DataPacket {
    @PowerNukkitOnly
    @Since("FUTURE")
    public static final byte NETWORK_ID = ProtocolInfo.ADD_VOLUME_ENTITY;

    private long id;
    private CompoundTag data;


    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        id = getUnsignedVarInt();
        data = getTag();
    }

    @Override
    public void encode() {
        putUnsignedVarInt(id);
        putTag(data);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public long getId() {
        return id;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setId(long id) {
        this.id = id;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public CompoundTag getData() {
        return data;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setData(CompoundTag data) {
        this.data = data;
    }
}
