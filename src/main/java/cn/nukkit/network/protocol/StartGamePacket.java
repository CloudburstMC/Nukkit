package cn.nukkit.network.protocol;

/**
 * Created on 15-10-13.
 */
public class StartGamePacket extends DataPacket {

    public static final byte NETWORK_ID = Info.START_GAME_PACKET;

    private int seed;

    private byte dimension;
    private int generator;
    private int gameMode;

    private long entityId;

    private int spawnX;
    private int spawnY;
    private int spawnZ;

    private float x;
    private float y;
    private float z;

    @Override
    public void decode() {
        ;
    }

    @Override
    public void encode() {
        reset();
        putInt(seed);
        putByte(dimension);
        putInt(generator);
        putInt(gameMode);
        putLong(entityId);
        putInt(spawnX);
        putInt(spawnY);
        putInt(spawnZ);
        putFloat(x);
        putFloat(y);
        putFloat(z);
        putByte((byte) 0);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
