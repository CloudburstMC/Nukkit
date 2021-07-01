package cn.nukkit.network.protocol;

public class MapCreateLockedCopyPacket extends DataPacket {

    public long originalMapId;
    public long newMapId;

    @Override
    public byte pid() {
        return ProtocolInfo.MAP_CREATE_LOCKED_COPY_PACKET;
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
