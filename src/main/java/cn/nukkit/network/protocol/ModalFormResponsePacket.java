package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerSettingsRespondedEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;

public class ModalFormResponsePacket extends DataPacket {

    public int formId;
    public String data;

    @Override
    public byte pid() {
        return ProtocolInfo.MODAL_FORM_RESPONSE_PACKET;
    }

    @Override
    public void decode() {
        this.formId = this.getVarInt();
        this.data = this.getString(); //Data will be null if player close form without submit (by cross button or ESC)
    }

    @Override
    public void encode() {

    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
