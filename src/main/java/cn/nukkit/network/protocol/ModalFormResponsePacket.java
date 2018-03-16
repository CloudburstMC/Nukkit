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
    public void handle(Player player) {
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        if (player.formWindows.containsKey(this.formId)) {
            FormWindow window = player.formWindows.remove(this.formId);
            window.setResponse(this.data.trim());

            PlayerFormRespondedEvent event = new PlayerFormRespondedEvent(player, this.formId, window);
            player.server.getPluginManager().callEvent(event);
        } else if (player.serverSettings.containsKey(this.formId)) {
            FormWindow window = player.serverSettings.get(this.formId);
            window.setResponse(this.data.trim());

            PlayerSettingsRespondedEvent event = new PlayerSettingsRespondedEvent(player, this.formId, window);
            player.server.getPluginManager().callEvent(event);

            //Set back new settings if not been cancelled
            if (!event.isCancelled() && window instanceof FormWindowCustom)
                ((FormWindowCustom) window).setElementsFromResponse();
        }
    }
}
