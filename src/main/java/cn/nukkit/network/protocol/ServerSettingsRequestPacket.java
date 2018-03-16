package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerServerSettingsRequestEvent;

import java.util.HashMap;

public class ServerSettingsRequestPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {

    }

    @Override
    public void handle(Player player) {
        PlayerServerSettingsRequestEvent settingsRequestEvent = new PlayerServerSettingsRequestEvent(player, new HashMap<>(player.serverSettings));
        player.getServer().getPluginManager().callEvent(settingsRequestEvent);

        if (!settingsRequestEvent.isCancelled()) {
            settingsRequestEvent.getSettings().forEach((id, window) -> {
                ServerSettingsResponsePacket re = new ServerSettingsResponsePacket();
                re.formId = id;
                re.data = window.getJSONData();
                player.dataPacket(re);
            });
        }
    }
}
