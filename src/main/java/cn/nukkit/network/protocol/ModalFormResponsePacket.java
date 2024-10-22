package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ModalFormResponsePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MODAL_FORM_RESPONSE_PACKET;

    public int formId;
    public String data = "null";
    public int cancelReason;

    @Override
    public byte pid() {
        return NETWORK_ID;
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
        this.encodeUnsupported();
    }
}
