package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetCommandsEnabledPacket extends DataPacket {

    public boolean enabled;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_COMMANDS_ENABLED_PACKET;
    }

    @Override
    public void decode() {
        this.enabled = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.enabled);
    }
}
