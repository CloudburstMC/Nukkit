package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ModalFormRequestPacket extends DataPacket {

    public long formId;
    public String formData;

    @Override
    public byte pid() {
        return ProtocolInfo.MODAL_FORM_REQUEST_PACKET;
    }

    @Override
    public void decode() {
    	this.formId = this.getUnsignedVarInt();
		this.formData = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.formId);
		this.putString(this.formData);
    }
}
