package cn.nukkit.level;

import cn.nukkit.level.generator.Generator;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public final class LevelInfo {

    public long time;

    public int gameType;

    private Class<? extends Generator> generator;

    public long lastPlayed;

    public String levelName;

    public boolean raining;

    public int rainTime;

    public float rainLevel;

    public boolean thundering;

    public int thunderTime;

    public float thunderLevel;

    public long randomSeed;

    public int spawnX;

    public int spawnY;

    public int spawnZ;

    public long currentTick;

    public boolean spawnMobs = true;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public Class<? extends Generator> getGenerator() {
        return generator;
    }

    public void setGenerator(Class<? extends Generator> generator) {
        this.generator = generator;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(long lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public boolean isRaining() {
        return raining;
    }

    public void setRaining(boolean raining) {
        this.raining = raining;
    }

    public int getRainTime() {
        return rainTime;
    }

    public void setRainTime(int rainTime) {
        this.rainTime = rainTime;
    }

    public float getRainLevel() {
        return rainLevel;
    }

    public void setRainLevel(float rainLevel) {
        this.rainLevel = rainLevel;
    }

    public boolean isThundering() {
        return thundering;
    }

    public void setThundering(boolean thundering) {
        this.thundering = thundering;
    }

    public int getThunderTime() {
        return thunderTime;
    }

    public void setThunderTime(int thunderTime) {
        this.thunderTime = thunderTime;
    }

    public float getThunderLevel() {
        return thunderLevel;
    }

    public void setThunderLevel(float thunderLevel) {
        this.thunderLevel = thunderLevel;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
    }

    public int getSpawnZ() {
        return spawnZ;
    }

    public void setSpawnZ(int spawnZ) {
        this.spawnZ = spawnZ;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(long currentTick) {
        this.currentTick = currentTick;
    }

    public boolean canSpawnMobs() {
        return spawnMobs;
    }

    public void setSpawnMobs(boolean spawnMobs) {
        this.spawnMobs = spawnMobs;
    }
}
