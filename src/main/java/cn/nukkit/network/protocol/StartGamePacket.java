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

    public long entityUniqueId;
    public long entityRuntimeId;
    public float x;
    public float y;
    public float z;
    public int seed;
    public byte dimension;
    public int generator = 1;
    public int gamemode;
    public int difficulty;
    public int spawnX;
    public int spawnY;
    public int spawnZ;
    public boolean hasAchievementsDisabled = true;
	public int dayCycleStopTime = -1; //-1 = not stopped, any positive value = stopped at that time
	public boolean eduMode = false;
	public float rainLevel;
	public float lightningLevel;
	public boolean commandsEnabled;
	public boolean isTexturePacksRequired = false;
    public String unknown;
	public String worldName;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityId(entityUniqueId);
        this.putEntityId(entityRuntimeId);
        this.putVector3f(this.x, this.y, this.z);
        this.putLFloat(0);
        this.putLFloat(0);
        this.putVarInt(seed);
        this.putVarInt(dimension);
        this.putVarInt(generator);
        this.putVarInt(gamemode);
        this.putVarInt(difficulty);
        this.putBlockCoords(spawnX, spawnY, spawnZ);
        this.putBoolean(hasAchievementsDisabled);
        this.putVarInt(dayCycleStopTime);
        this.putBoolean(eduMode);
        this.putLFloat(rainLevel);
        this.putLFloat(lightningLevel);
        this.putBoolean(commandsEnabled);
        this.putBoolean(isTexturePacksRequired);
        this.putString(unknown);
        this.putString(worldName);
    }

}
