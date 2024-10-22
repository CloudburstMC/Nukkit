package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class MapCreateLockedCopyPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MAP_CREATE_LOCKED_COPY_PACKET;

    public long originalMapId;
    public long newMapId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.originalMapId = this.getVarLong();
        this.newMapId = this.getVarLong();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarLong(this.originalMapId);
        this.putVarLong(this.newMapId);
    }
}
