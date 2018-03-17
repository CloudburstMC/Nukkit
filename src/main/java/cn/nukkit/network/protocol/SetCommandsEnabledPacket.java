package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class SetCommandsEnabledPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_COMMANDS_ENABLED_PACKET;

    public boolean enabled;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.enabled);
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
