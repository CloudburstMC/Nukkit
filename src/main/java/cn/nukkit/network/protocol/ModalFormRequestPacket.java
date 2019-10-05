package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class ModalFormRequestPacket extends DataPacket {

    public int formId;
    public String data;

    @Override
    public short pid() {
        return ProtocolInfo.MODAL_FORM_REQUEST_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVarInt(buffer, this.formId);
        Binary.writeString(buffer, this.data);
    }
}
