package cn.nukkit.network.protocol;

public class ModalFormResponsePacket extends DataPacket {

    public int formId;
    public String data;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("MODAL_FORM_RESPONSE_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.formId = this.getVarInt();
        this.data = this.getString(); //Data will be null if player close form without submit (by cross button or ESC)
    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }
}
