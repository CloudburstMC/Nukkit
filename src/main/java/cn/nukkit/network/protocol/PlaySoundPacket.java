package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class PlaySoundPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAY_SOUND_PACKET;

    public String name;
    public int x;
    public int y;
    public int z;
    public float volume;
    public float pitch;

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
        this.putString(this.name);
        this.putBlockVector3(this.x * 8, this.y * 8, this.z * 8);
        this.putLFloat(this.volume);
        this.putLFloat(this.pitch);
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
