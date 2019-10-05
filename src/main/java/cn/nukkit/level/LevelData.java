package cn.nukkit.level;


import cn.nukkit.level.gamerule.GameRuleMap;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.math.Vector3;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

@Data
public class LevelData {
    private static final AtomicLongFieldUpdater<LevelData> currentTickUpdater = AtomicLongFieldUpdater.newUpdater(
            LevelData.class, "currentTick");

    private final Map<String, Object> generatorOptions = new HashMap<>();
    @Getter(AccessLevel.NONE)
    private final GameRuleMap gameRules = new GameRuleMap();
    private volatile long currentTick;
    private long seed = ThreadLocalRandom.current().nextLong();
    private int dimension;
    private int generator;
    private String name = "World";
    private int time;
    private Vector3 spawn = new Vector3(0, 128, 0);
    private int serverChunkTickRange;
    private int spawnRadius;
    private int rainTime;
    private int lightningTime;
    private float rainLevel;
    private float lightningLevel;
    private int difficulty;
    private int storageVersion;
    private int gameType;
    private Vector3 limitedWorldOrigin;
    private int netherScale;
    private int networkVersion;
    private int platform;
    private long worldStartCount;
    private long lastPlayed;

    private boolean bonusChestEnabled;
    private boolean bonusChestSpawned;
    private boolean forceGameType;
    private boolean educationFeaturesEnabled;
    private boolean eduLevel;
    private boolean hasBeenLoadedInCreative;
    private boolean hasLockedBehaviorPack;
    private boolean hasLockedResourcePack;
    private boolean immutableWorld;
    private boolean texturePacksRequired;


    public LevelData() {
    }

    public LevelData(LevelData levelData) {
        this.generatorOptions.putAll(levelData.generatorOptions);
        this.gameRules.putAll(levelData.gameRules);
        this.generator = levelData.generator;
        this.name = levelData.name;
        this.spawn = levelData.spawn;
        this.serverChunkTickRange = levelData.serverChunkTickRange;
        this.spawnRadius = levelData.spawnRadius;
        this.rainTime = levelData.rainTime;
        this.lightningTime = levelData.lightningTime;
        this.rainLevel = levelData.rainLevel;
        this.lightningLevel = levelData.lightningLevel;
        this.difficulty = levelData.difficulty;
    }

    public GameRuleMap getGameRules() {
        return this.gameRules;
    }

    public void checkTime(int tickRate) {
        if (this.gameRules.get(GameRules.DO_DAYLIGHT_CYCLE)) {
            this.time += tickRate;
        }
    }

    void tick() {
        currentTickUpdater.incrementAndGet(this);
    }

    public void addTime(int time) {
        this.time += time;
    }
}
