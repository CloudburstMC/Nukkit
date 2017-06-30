package cn.nukkit.network.protocol;

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
        this.putString(this.name);
        this.putBlockCoords(this.x, this.y, this.z);
        this.putLFloat(this.volume);
        this.putLFloat(this.pitch);
    }
}
