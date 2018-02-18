package cn.nukkit.network.protocol;

public class ModalFormRequestPacket extends DataPacket {

    public int formId;
    public String data;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("MODAL_FORM_REQUEST_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putVarInt(this.formId);
        this.putString(this.data);
    }
}
