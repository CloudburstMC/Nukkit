package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class ModalFormResponsePacket extends DataPacket {

    public int formId;
    public String data;

    @Override
    public short pid() {
        return ProtocolInfo.MODAL_FORM_RESPONSE_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.formId = Binary.readVarInt(buffer);
        this.data = Binary.readString(buffer); //Data will be null if player close form without submit (by cross button or ESC)
    }

    @Override
    protected void encode(ByteBuf buffer) {

    }
}
