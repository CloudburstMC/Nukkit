package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ModalFormRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MODAL_FORM_REQUEST_PACKET;

    public int formId;
    public String data;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.formId);
        this.putString(this.data);
    }
}
