package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class DebugInfoPacket extends DataPacket {

    public long entityUniqueId;
    public String data;

    @Override
    public byte pid() {
        return ProtocolInfo.DEBUG_INFO_PACKET;
    }

    @Override
    public void decode() {
        this.entityUniqueId = this.getEntityUniqueId();
        this.data = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putString(this.data);
    }
}
