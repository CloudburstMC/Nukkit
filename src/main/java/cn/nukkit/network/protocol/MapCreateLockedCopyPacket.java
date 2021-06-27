package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class MapCreateLockedCopyPacket extends DataPacket {

    public long mapUniqueId;
    public long newMapUniqueId;

    @Override
    public byte pid() {
        return ProtocolInfo.MAP_CREATE_LOCKED_COPY_PACKET;
    }

    @Override
    public void decode() {
        this.mapUniqueId = this.getEntityUniqueId();
        this.newMapUniqueId = this.getEntityUniqueId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.mapUniqueId);
        this.putEntityUniqueId(this.newMapUniqueId);
    }
}
