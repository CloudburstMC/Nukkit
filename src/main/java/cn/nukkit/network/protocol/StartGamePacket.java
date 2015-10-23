package cn.nukkit.network.protocol;

/**
 * Created on 15-10-13.
 */
public class StartGamePacket extends DataPacket {

    public static final byte NETWORK_ID = Info.START_GAME_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    private int seed;

    private byte dimension;
    private int generator;
    private int gameMode;

    private long eid;

    private int spawnX;
    private int spawnY;
    private int spawnZ;

    private float x;
    private float y;
    private float z;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(seed);
        this.putByte(dimension);
        this.putInt(generator);
        this.putInt(gameMode);
        this.putLong(eid);
        this.putInt(spawnX);
        this.putInt(spawnY);
        this.putInt(spawnZ);
        this.putFloat(x);
        this.putFloat(y);
        this.putFloat(z);
        this.putByte((byte) 0);
    }

}
