package cn.nukkit.network.protocol;

/**
 * Created on 15-10-13.
 */
public class StartGamePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.START_GAME_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int seed;

    public byte dimension;
    public int generator;
    public int gamemode;

    public long eid;

    public int spawnX;
    public int spawnY;
    public int spawnZ;

    public float x;
    public float y;
    public float z;

    public boolean b1;
    public boolean b2;
    public boolean b3;
    public String unknownstr;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(seed);
        this.putByte(dimension);
        this.putInt(generator);
        this.putInt(gamemode);
        this.putLong(eid);
        this.putInt(spawnX);
        this.putInt(spawnY);
        this.putInt(spawnZ);
        this.putFloat(x);
        this.putFloat(y);
        this.putFloat(z);
        this.putBoolean(b1);
        this.putBoolean(b2);
        this.putBoolean(b3);
        this.putString(unknownstr);
        this.putByte((byte) 0);
    }

}
