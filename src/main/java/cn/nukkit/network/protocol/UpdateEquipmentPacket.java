package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString(exclude = "namedtag")
public class UpdateEquipmentPacket extends DataPacket {

    public int windowId;
    public int windowType;
    public int unknown; //TODO: find out what this is (vanilla always sends 0)
    public long eid;
    public byte[] namedtag;


    @Override
    public short pid() {
        return ProtocolInfo.UPDATE_EQUIPMENT_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeByte((byte) this.windowId);
        buffer.writeByte((byte) this.windowType);
        Binary.writeEntityUniqueId(buffer, this.eid);
        buffer.writeBytes(this.namedtag);
    }
}
