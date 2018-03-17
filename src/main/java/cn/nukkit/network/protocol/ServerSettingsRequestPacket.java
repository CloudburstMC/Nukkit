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
    protected void handle(Player player) {
        player.handle(this);
    }
}
