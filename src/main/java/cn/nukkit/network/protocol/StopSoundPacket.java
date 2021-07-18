package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class StopSoundPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.STOP_SOUND_PACKET;

    public String soundName;
    public boolean stopAll;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.soundName = this.getString();
        this.stopAll = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.soundName);
        this.putBoolean(this.stopAll);
    }
}
