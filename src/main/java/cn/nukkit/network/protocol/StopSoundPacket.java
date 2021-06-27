package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class StopSoundPacket extends DataPacket {

    public String soundName;
    public boolean stopAll;

    @Override
    public byte pid() {
        return ProtocolInfo.STOP_SOUND_PACKET;
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
