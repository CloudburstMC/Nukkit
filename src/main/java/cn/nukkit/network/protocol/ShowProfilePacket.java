package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class ShowProfilePacket extends DataPacket {

    public String xuid;

    @Override
    public byte pid() {
        return ProtocolInfo.SHOW_PROFILE_PACKET;
    }

    @Override
    public void decode() {
        this.xuid = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.xuid);
    }
}
