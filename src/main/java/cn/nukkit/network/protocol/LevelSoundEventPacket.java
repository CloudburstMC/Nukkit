package cn.nukkit.network.protocol;


import cn.nukkit.math.Vector3f;

public class LevelSoundEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.LEVEL_SOUND_EVENT_PACKET;

    public byte sound;
    public float x;
    public float y;
    public float z;
    public int volume;
    public int pitch;
    public boolean unknownBool;
    public boolean unknownBool2;

    @Override
    public void decode() {
        this.sound = (byte) this.getByte();
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.volume = this.getVarInt();
        this.pitch = this.getVarInt();
        this.unknownBool = this.getBoolean();
        this.unknownBool2 = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.sound);
        this.putVector3f(this.x, this.y, this.z);
        this.putVarInt(this.volume);
        this.putVarInt(this.pitch);
        this.putBoolean(this.unknownBool);
        this.putBoolean(this.unknownBool2);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
