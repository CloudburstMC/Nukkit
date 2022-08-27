package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ModalFormResponsePacket extends DataPacket {

    public int formId;
    public String data = "null";
    public int cancelReason;

    @Override
    public byte pid() {
        return ProtocolInfo.MODAL_FORM_RESPONSE_PACKET;
    }

    @Override
    public void decode() {
        this.formId = this.getVarInt();
        if (this.getBoolean()) {
            this.data = this.getString();
        }
        if (this.getBoolean()) {
            this.cancelReason = this.getByte();
        }
    }

    @Override
    public void encode() {

    }
}
