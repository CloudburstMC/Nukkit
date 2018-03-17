package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class ModalFormRequestPacket extends DataPacket {

    public int formId;
    public String data;

    @Override
    public byte pid() {
        return ProtocolInfo.MODAL_FORM_REQUEST_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.formId);
        this.putString(this.data);
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
