package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import lombok.ToString;

@ToString
public class PlaySoundPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAY_SOUND_PACKET;

    public String soundName;
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
        this.soundName = this.getString();
        BlockVector3 blockVector3 = this.getBlockVector3();
        this.x = blockVector3.getX() / 8;
        this.y = blockVector3.getY() / 8;
        this.z = blockVector3.getZ() / 8;
        this.volume = this.getLFloat();
        this.pitch = this.getLFloat();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.soundName);
        this.putBlockVector3(this.x * 8, this.y * 8, this.z * 8);
        this.putLFloat(this.volume);
        this.putLFloat(this.pitch);
    }
}
