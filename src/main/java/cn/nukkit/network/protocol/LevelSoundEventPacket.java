package cn.nukkit.network.protocol;


import cn.nukkit.math.Vector3f;

public class LevelSoundEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.LEVEL_SOUND_EVENT_PACKET;

    // silence if case2==1
    public static final int SOUND_BLOCK_PLACE = 1;
    // block break sound when pitch==1, footstep when pitch==2. case1-> block id
    public static final int SOUND_BLOCK_BREAK = 2;
    // silence if case2==1
    public static final int SOUND_BLOCK_FOOTSTEP = 4;
    // When type -> SOUND_NOTE,  case1 -> instrument type, case2 -> pitch
    public static final int SOUND_NOTE = 32;

    public int type;
    public float x;
    public float y;
    public float z;
    public int case1;
    public int case2;
    public boolean unknownBool;
    public boolean unknownBool2;

    @Override
    public void decode() {
        this.type = this.getVarInt();
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.case1 = this.getVarInt();
        this.case2 = this.getVarInt();
        this.unknownBool = this.getBoolean();
        this.unknownBool2 = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.type);
        this.putVector3f(this.x, this.y, this.z);
        this.putVarInt(this.case1);
        this.putVarInt(this.case2);
        this.putBoolean(this.unknownBool);
        this.putBoolean(this.unknownBool2);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
