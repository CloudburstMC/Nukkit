package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;

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
        this.name = this.getString();
        BlockVector3 v = this.getBlockVector3();
        this.x = v.x / 8;
        this.y = v.y / 8;
        this.z = v.z / 8;
        this.volume = this.getLFloat();
        this.pitch = this.getLFloat();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.name);
        this.putBlockVector3(this.x * 8, this.y * 8, this.z * 8);
        this.putLFloat(this.volume);
        this.putLFloat(this.pitch);
    }
}
